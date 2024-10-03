package co.edu.uniquindio.UniEventos.dto.EventoDTOs;

import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;

import java.util.Objects;

public record FiltroEventoDTO(
        String nombre,
        String ciudad,
        String tipo,
        String fechaInicio,
        String fechaFin
) {

    // Método equalsIgnoreCase para comparación de cadenas (ignorar mayúsculas y minúsculas)
    public boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }

    // Validaciones
    public boolean isValidName(String eventName) {
        return eventName != null && !eventName.trim().isEmpty() && equalsIgnoreCase(eventName, nombre);
    }

    public boolean isValidCity(String eventCity) {
        return eventCity != null && !eventCity.trim().isEmpty() && equalsIgnoreCase(eventCity, ciudad);
    }

    public boolean isValidType(String eventType) {
        return eventType != null && !eventType.trim().isEmpty() && equalsIgnoreCase(eventType, tipo);
    }

    // Validación de rango de fechas (simple, puede ser adaptado según el formato)
    public boolean isWithinDateRange(String eventDate) {
        if (fechaInicio != null && fechaFin != null) {
            return eventDate.compareTo(fechaInicio) >= 0 && eventDate.compareTo(fechaFin) <= 0;
        }
        return true;
    }

    // Método matches para comparar un evento con los filtros
    public boolean matches(Evento evento) {
        boolean matchesNombre = (nombre == null || nombre.isEmpty() || equalsIgnoreCase(evento.getNombre(), nombre));
        boolean matchesCiudad = (ciudad == null || ciudad.isEmpty() || equalsIgnoreCase(evento.getCiudad(), ciudad));
        boolean matchesTipo = (tipo == null || tipo.isEmpty() || equalsIgnoreCase(evento.getTipo(), tipo));
        boolean matchesFecha = isWithinDateRange(evento.getFecha());

        return matchesNombre && matchesCiudad && matchesTipo && matchesFecha;
    }

    // Método toString generado automáticamente por el record (puedes añadir lógica si es necesario)

    // equals() y hashCode() generados automáticamente por el record
}
