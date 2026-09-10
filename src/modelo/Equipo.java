package modelo;

public class Equipo {
    private String ip;
    private String nombre;
    private boolean activo;
    private long tiempoRespuesta;

    public Equipo(String ip, String nombre, boolean activo, long tiempoRespuesta) {
        this.ip = ip;
        this.nombre = nombre;
        this.activo = activo;
        this.tiempoRespuesta = tiempoRespuesta;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public long getTiempoRespuesta() {
        return tiempoRespuesta;
    }

    public void setTiempoRespuesta(long tiempoRespuesta) {
        this.tiempoRespuesta = tiempoRespuesta;
    }
}