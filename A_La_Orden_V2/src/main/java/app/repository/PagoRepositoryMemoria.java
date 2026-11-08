package app.repository;

import app.domain.Pago;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Implementa el almacenamiento temporal de pagos en memoria.
public class PagoRepositoryMemoria implements PagoRepository {

    private final Map<Integer, Pago> pagos;
    private int siguienteId;

    public PagoRepositoryMemoria() {
        pagos = new LinkedHashMap<>();
        siguienteId = 1;
    }

    // Guarda un pago y devuelve el pago registrado.
    @Override
    public Pago guardar(Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException(
                    "El pago no puede ser nulo."
            );
        }

        if (pago.getId() != 0) {
            throw new IllegalArgumentException(
                    "El pago nuevo no debe tener un identificador asignado."
            );
        }

        // Generamos el identificador del pago.
        Pago pagoRegistrado = pago.conIdentificadorGenerado(siguienteId);

        pagos.put(pagoRegistrado.getId(), pagoRegistrado);
        siguienteId++;

        return pagoRegistrado;
    }

    // Actualiza un pago existente.
    @Override
    public void actualizar(Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException(
                    "El pago no puede ser nulo."
            );
        }

        if (!pagos.containsKey(pago.getId())) {
            throw new IllegalArgumentException(
                    "No existe un pago con ese identificador."
            );
        }

        pagos.put(pago.getId(), pago);
    }

    // Busca un pago por su identificador.
    @Override
    public Pago buscarPorId(int id) {
        return pagos.get(id);
    }

    // Consulta los pagos asociados a un pedido.
    @Override
    public List<Pago> listarPorPedido(int pedidoId) {

        List<Pago> resultado = new ArrayList<>();

        for (Pago pago : pagos.values()) {
            if (pago.getPedido().getId() == pedidoId) {
                resultado.add(pago);
            }
        }

        return Collections.unmodifiableList(resultado);
    }

    // Consulta todos los pagos registrados.
    @Override
    public List<Pago> listar() {
        return Collections.unmodifiableList(
                new ArrayList<>(pagos.values())
        );
    }
}