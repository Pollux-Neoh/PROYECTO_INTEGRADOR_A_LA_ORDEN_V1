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

    public PagoRepositoryMemoria() {
        pagos = new LinkedHashMap<>();
    }

    @Override
    public void guardar(Pago pago) {
        if (pago == null){
            throw new IllegalArgumentException(
                    "El pago no puede ser nulo."
            );
        }
        if (pagos.containsKey(pago.getId())){
            throw new IllegalArgumentException(
                    "El pago ya existe con ese identificador."
            );
        }
        pagos.put(pago.getId(), pago);
    }

    @Override
    public void actualizar(Pago pago) {

        // Validamos que el pago no sea nulo.
        if (pago == null) {
            throw new IllegalArgumentException(
                    "El pago no puede ser nulo."
            );
        }

        // Comprobamos que el pago exista en el repositorio.
        if (!pagos.containsKey(pago.getId())) {
            throw new IllegalArgumentException(
                    "No existe un pago con ese identificador."
            );
        }

        // Actualizamos el pago existente.
        pagos.put(pago.getId(), pago);
    }

    @Override
    public Pago buscarPorId(int id){
        return pagos.get(id);
    }
    @Override
    public List<Pago> listarPorPedido(int pedidoId){

        // Creamos una lista para almacenar los resultados.
        List<Pago> resultado = new ArrayList<>();

        // Recorremos los pagos almacenados en el Map
        for (Pago pago : pagos.values()){

            // Comprobamos si el pago pertenece al pedido solicitado
            if (pago.getPedido().getId() == pedidoId){
                resultado.add(pago);
            }
        }

        // Devolvemos una lista que no puede modificarse externamente
        return Collections.unmodifiableList(resultado);
    }

    @Override
    public List<Pago> listar(){
        return Collections.unmodifiableList(
                new ArrayList<>(pagos.values())
        );
    }
}
