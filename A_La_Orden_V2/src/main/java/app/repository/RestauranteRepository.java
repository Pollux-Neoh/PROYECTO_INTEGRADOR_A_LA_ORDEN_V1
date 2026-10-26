
      package app.repository;

      import app.domain.Restaurante;
      import java.util.List;
      import java.util.Optional;

     // Una interfaz define "QUÉ" operaciones debe ofrecer la capa de datos,
     // pero no dice "CÓMO" las hace.
     public interface RestauranteRepository {

    // Metodo que devuelve todos los guardados
    List<Restaurante> obtenerTodos();
    Optional<Restaurante> buscarPorId(int id);
}
