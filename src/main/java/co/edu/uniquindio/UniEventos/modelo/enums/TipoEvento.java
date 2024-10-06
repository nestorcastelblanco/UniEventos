package co.edu.uniquindio.UniEventos.modelo.enums;

public enum TipoEvento {
    CONCIERTO, CULTURAL, DEPORTE, MODA, BELLEZA;

    public boolean equalsIgnoreCase(String tipo) {
        if (tipo == null) {
            return false;
        }
        // Verificar si el nombre del enum es igual al string ignorando mayúsculas/minúsculas
        return this.name().equalsIgnoreCase(tipo);
    }
}
