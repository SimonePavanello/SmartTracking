package it.univr.track.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {

    @NotBlank(message = "Lo username è obbligatorio")
    @Size(min = 4, max = 20, message = "Lo username deve essere tra 4 e 20 caratteri")
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
    private String password;

    private String confirmPassword;

    @NotBlank(message = "Selezionare un ruolo")
    private String role; // ADMIN o USER

    // Getter e Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    /**
     * Business logic nel DTO per validare la corrispondenza delle password.
     */
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
