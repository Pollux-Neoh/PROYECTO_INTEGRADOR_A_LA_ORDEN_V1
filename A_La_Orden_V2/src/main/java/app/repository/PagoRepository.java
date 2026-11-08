
package app.repository;

import app.domain.Pago;

import java.util.List;

// Define las operaciones de almacenamiento de los pagos.
public interface PagoRepository {

    // Guarda un pago y devuelve el pago registrado con su ID.
    Pago guardar(Pago pago);

    // Actualiza un pago que ya existe.
    void actualizar(Pago pago);

    // Busca un pago por su identificador.
    Pago buscarPorId(int id);

    // Consulta los pagos asociados a un pedido.
    List<Pago> listarPorPedido(int pedidoId);

    // Consulta todos los pagos registrados.
    List<Pago> listar();
}