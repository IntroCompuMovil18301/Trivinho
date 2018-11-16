package javeriana.compumovil.tcp.trivinho;

public class Utils {

    static final String PATH_USERS = "usuarios/";
    static final String PATH_HUESPEDES = "huespedes/";
    static final String PATH_ANFITRIONES = "anfitriones/";
    private static String PATH_FECHAS = "fechasdisponibles/";
    
    
    private static String PATH_ALOJAMIENTOS = "alojamientos/";
    private static String PATH_FOTOSALOJAMIENTO = "fotosalojamientos/";

    public static boolean isEmailValid(String email) {
        boolean isValid = true;
        if (!email.contains("@") || !email.contains(".") || email.length() < 5)
            isValid = false;
        return isValid;
    }

    public static String getPathUsers() {
        return PATH_USERS;
    }

    public static String getPathHuespedes() {
        return PATH_HUESPEDES;
    }

    public static String getPathAnfitriones() {
        return PATH_ANFITRIONES;
    }

    public static String getPathFechas() {
        return PATH_FECHAS;
    }

    public static String getPathAlojamientos() {
        return PATH_ALOJAMIENTOS;
    }

    public static String getPathFotosalojamiento() {
        return PATH_FOTOSALOJAMIENTO;
    }
}
