package app.validators;


public class ClienteValidator {

    public static boolean validarCorreo(String correo) {

        return correo != null
                && correo.contains("@")
                && correo.contains(".");
    }
    public static boolean validarContrasenia(String contrasenia){

        return contrasenia != null
                && contrasenia.length() >= 8;
    }
}
