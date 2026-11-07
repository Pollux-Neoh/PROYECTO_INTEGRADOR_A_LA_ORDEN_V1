package app.service;

import app.domain.Cliente;
import app.domain.DetallePedido;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.enums.EstadoPedido;
import app.repository.PedidoRepository;
import app.validators.PedidoValidator;
import app.domain.Restaurante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final NotificacionService notificacionService;

    public PedidoService(PedidoRepository pedidoRepository) {
        this(pedidoRepository, new NotificacionService());
    }

    public PedidoService(PedidoRepository pedidoRepository, NotificacionService notificacionService) {
        this.pedidoRepository = pedidoRepository;
        this.notificacionService = notificacionService;
    }

    public Pedido crearPedido(int id, Cliente cliente) {
        return crearPedido(id, cliente, null);
    }

    public Pedido crearPedido(int id, Cliente cliente, Restaurante restaurante) {
        Pedido pedido = new Pedido(id, cliente, restaurante);
        if (!PedidoValidator.validarPedido(pedido) || pedidoRepository.buscarPorId(id) != null) {
            return null;
        }
        pedidoRepository.guardar(pedido);
        return pedido;
    }

    // Genera automáticamente el ID de cada nuevo pedido.
    public Pedido crearPedido(Cliente cliente, Restaurante restaurante) {

        int siguienteId = 1003;

        while (pedidoRepository.buscarPorId(siguienteId) != null) {
            siguienteId++;
        }

        return crearPedido(siguienteId, cliente, restaurante);
    }

    public Pedido buscarPorId(int id) {
        return pedidoRepository.buscarPorId(id);
    }

    public boolean terminarPedido(int pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido == null || !pedido.terminar()) {
            return false;
        }
        pedidoRepository.guardar(pedido);
        return true;
    }



    public boolean agregarProducto(int pedidoId, Producto producto, int cantidad) {

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        if (pedido == null
                || pedido.getEstado() != EstadoPedido.PENDIENTE_CONFIRMACION
                || !PedidoValidator.validarProducto(producto)
                || !PedidoValidator.validarCantidad(cantidad)) {
            return false;
        }

        for (DetallePedido detalle : pedido.getDetalles()) {

            if (detalle.getProducto().getId() == producto.getId()
                    && cantidad > Integer.MAX_VALUE - detalle.getCantidad()) {
                return false;
            }
        }

        pedido.agregarProducto(producto, cantidad);
        pedidoRepository.guardar(pedido);

        return true;
    }



    public boolean actualizarCantidad(int pedidoId, int productoId, int cantidad) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido == null
                || pedido.getEstado() != EstadoPedido.PENDIENTE_CONFIRMACION
                || !PedidoValidator.validarCantidad(cantidad)) {
            return false;
        }
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getProducto().getId() == productoId) {
                if (cantidad > detalle.getCantidad()
                        && !PedidoValidator.validarProducto(detalle.getProducto())) {
                    return false;
                }
                break;
            }
        }
        if (!pedido.actualizarCantidad(productoId, cantidad)) {
            return false;
        }
        pedidoRepository.guardar(pedido);
        return true;
    }

    public boolean quitarProducto(int pedidoId, int productoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido == null
                || pedido.getEstado() != EstadoPedido.PENDIENTE_CONFIRMACION
                || !pedido.quitarProducto(productoId)) {
            return false;
        }
        pedidoRepository.guardar(pedido);
        return true;
    }

    public BigDecimal obtenerSubtotal(int pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        return pedido == null ? null : pedido.getSubtotal();
    }

    // Calcula el tiempo estimado según los pedidos activos del restaurante.
    public int calcularTiempoEstimado(int restauranteId) {

        int pedidosActivos = 0;

        for (Pedido pedido : pedidoRepository.listar()) {

            if (pedido.getRestaurante() != null
                    && pedido.getRestaurante().getId() == restauranteId
                    && (pedido.getEstado() == EstadoPedido.CONFIRMADO
                    || pedido.getEstado() == EstadoPedido.EN_PROCESO)) {

                pedidosActivos++;
            }
        }

        return 10 + (5 * pedidosActivos);
    }


    // Permite al comerciante ajustar el tiempo estimado.
    public boolean ajustarTiempoEstimado(int pedidoId, int minutos) {

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        if (pedido == null
                || (pedido.getEstado() != EstadoPedido.CONFIRMADO
                && pedido.getEstado() != EstadoPedido.EN_PROCESO)) {
            return false;
        }

        if (!pedido.actualizarTiempoEstimado(minutos)) {
            return false;
        }

        pedidoRepository.guardar(pedido);

        // Notifica al cliente el tiempo actualizado
        notificacionService.notificarTiempoEstimado(pedido);

        return true;
    }



    public boolean confirmarPedido(int pedidoId) {

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        if (!PedidoValidator.validarConfirmacion(pedido) || !pedido.confirmar()) {
            return false;
        }

        // Calcula el tiempo estimado
        int tiempoEstimado = calcularTiempoEstimado(
                pedido.getRestaurante().getId()
        );

        pedido.actualizarTiempoEstimado(tiempoEstimado);

        pedidoRepository.guardar(pedido);

        // Notifica al comerciante
        notificacionService.notificarPedidoConfirmado(pedido);

        // Notifica al cliente
        notificacionService.notificarTiempoEstimado(pedido);

        return true;
    }

    public List<Pedido> obtenerPedidosEntrantes(int restauranteId) {

        List<Pedido> pedidosEntrantes = new ArrayList<>();

        if (restauranteId <= 0) {
            return pedidosEntrantes;
        }

        for (Pedido pedido : pedidoRepository.listar()) {

            if (pedido.getRestaurante() != null
                && pedido.getRestaurante().getId() == restauranteId
                && pedido.getEstado() == EstadoPedido.CONFIRMADO) {

                pedidosEntrantes.add(pedido);
            }
        }
        return pedidosEntrantes;
    }

    public boolean aceptarPedido(int pedidoId, int restauranteId) {

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        if (pedido == null
            || pedido.getRestaurante() == null
            || pedido.getRestaurante().getId() != restauranteId
            || !pedido.aceptar()) {

            return false;
        }

        pedidoRepository.guardar(pedido);
        return true;
    }

    public boolean rechazarPedido(int pedidoId, int restauranteId) {

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        if (pedido == null
            || pedido.getRestaurante() == null
            || pedido.getRestaurante().getId() != restauranteId
            || !pedido.rechazar()) {
            return false;
        }

        pedidoRepository.guardar(pedido);
        return true;
    }
    public NotificacionService getNotificacionService() {
        return notificacionService;
    }
}
