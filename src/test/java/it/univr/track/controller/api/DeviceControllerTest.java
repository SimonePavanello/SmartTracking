package it.univr.track.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.univr.track.dto.DeviceConfigDTO;
import it.univr.track.entity.Device;
import it.univr.track.service.DeviceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("UC1 - Registrazione device (Solo Admin)")
    @WithMockUser(roles = "ADMIN")
    void testAddDeviceAdmin() throws Exception {
        Device device = new Device();
        device.setUuid("SN-999");

        when(deviceService.registerNewDevice("SN-999")).thenReturn(device);

        mockMvc.perform(post("/api/device")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value("SN-999"));
    }

    @Test
    @DisplayName("UC1 - Blocco registrazione device per utenti non Admin")
    @WithMockUser(roles = "USER")
    void testAddDeviceUserDenied() throws Exception {
        mockMvc.perform(post("/api/device")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"uuid\":\"SN-999\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Lettura Configurazione - Successo con API Key")
    @WithMockUser
    void testReadDeviceConfigSuccess() throws Exception {
        Device device = new Device();
        device.setUuid("SN-123");
        device.setApiKey("valid-key");

        when(deviceService.getByUid("SN-123")).thenReturn(Optional.of(device));

        mockMvc.perform(get("/api/device/SN-123")
                        .header("X-API-KEY", "valid-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value("SN-123"));
    }

    @Test
    @DisplayName("Lettura Configurazione - API Key non valida")
    @WithMockUser
    void testReadDeviceConfigUnauthorized() throws Exception {
        Device device = new Device();
        device.setUuid("SN-123");
        device.setApiKey("valid-key");

        when(deviceService.getByUid("SN-123")).thenReturn(Optional.of(device));

        mockMvc.perform(get("/api/device/SN-123")
                        .header("X-API-KEY", "wrong-key"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("API Key not valid"));
    }

    @Test
    @DisplayName("UC2 - Modifica configurazione dispositivo (API)")
    @WithMockUser
    void testEditDevice() throws Exception {
        DeviceConfigDTO configDto = new DeviceConfigDTO();
        configDto.setUuid("SN-123");
        configDto.setInterval(30);

        mockMvc.perform(put("/api/device/api/device")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(configDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(deviceService).updateConfiguration(any(DeviceConfigDTO.class));
    }

    @Test
    @DisplayName("UC8 - Dismissione dispositivo (API - Solo Admin)")
    @WithMockUser(roles = "ADMIN")
    void testDecommissionDeviceApi() throws Exception {
        mockMvc.perform(delete("/api/device/SN-123"))
                .andExpect(status().isNoContent());

        verify(deviceService).decommissionDevice("SN-123");
    }

    @Test
    @DisplayName("Lista Dispositivi (API)")
    @WithMockUser
    void testGetDevicesListApi() throws Exception {
        Device d1 = new Device();
        d1.setUuid("SN-1");

        when(deviceService.getAllDevices()).thenReturn(List.of(d1));

        mockMvc.perform(get("/api/device/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value("SN-1"));
    }
}