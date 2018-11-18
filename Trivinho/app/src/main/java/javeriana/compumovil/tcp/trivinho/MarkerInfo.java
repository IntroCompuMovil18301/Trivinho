package javeriana.compumovil.tcp.trivinho;


public class MarkerInfo {

    private String tipo;
    private String moneda;
    private String precio;
    private String descripion;
    private String rutaFoto;
    private String id;
    private float rating;

    public MarkerInfo(String tipo, String moneda, String precio, String descripion, String rutaFoto, String id, float rating) {
        this.tipo = tipo;
        this.moneda = moneda;
        this.precio = precio;
        this.descripion = descripion;
        this.rutaFoto = rutaFoto;
        this.id = id;
        this.rating = rating;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDescripion() {
        return descripion;
    }

    public void setDescripion(String descripion) {
        this.descripion = descripion;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
