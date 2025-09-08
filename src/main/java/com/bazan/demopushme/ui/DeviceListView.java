package com.bazan.demopushme.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.bazan.demopushme.entity.DeviceToken;
import com.bazan.demopushme.service.TokenService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Vista de Vaadin para mostrar la lista de dispositivos registrados.
 * Esta clase sirve como una interfaz de usuario simple para ver los tokens de dispositivo
 * almacenados en la base de datos.
 */
@Route(value = "dispositivos", layout = MainLayout.class)
@PageTitle("Dispositivos Registrados")
public class DeviceListView extends VerticalLayout {

    private final TokenService tokenService;
     // Se inicializa el Grid sin el tipo de bean para controlar las columnas manualmente.
    private final Grid<DeviceToken> deviceTokenGrid = new Grid<>();

    /**
     * Constructor que recibe el TokenService inyectado por Spring.
     */
    @Autowired
    public DeviceListView(TokenService tokenService) {
        this.tokenService = tokenService;
        setupUI();
    }

     /**
     * Configura el layout y los componentes de la vista.
     */
    private void setupUI() {
        // Título de la vista
        H1 title = new H1("Dispositivos Registrados");
        add(title);

        // Configurar la grilla para mostrar los DeviceToken
        // Se puede personalizar las columnas si es necesario.
        deviceTokenGrid.addColumn(DeviceToken::getId).setHeader("ID").setSortable(true);

        deviceTokenGrid.addColumn(DeviceToken::getUserId).setHeader("ID de Usuario").setSortable(true);

        deviceTokenGrid.addColumn(DeviceToken::getDeviceId).setHeader("ID de Dispositivo").setSortable(true);

        deviceTokenGrid.addColumn(DeviceToken::getFirebaseToken).setHeader("Token de Firebase").setSortable(true);

        deviceTokenGrid.addColumn(deviceToken -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            return deviceToken.getRegisteredAt().format(formatter);
        }).setHeader("Fecha de Registro").setKey("registeredAt").setSortable(true);


         // Obtener la lista de dispositivos y llenar la grilla
        List<DeviceToken> allDevices = tokenService.getAllRegisteredDevices();
        deviceTokenGrid.setItems(allDevices);
        
        // Añadir la grilla al layout de la vista
        add(deviceTokenGrid);
        
        // Estilos para la vista
        addClassName("device-list-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setSpacing(true);
    }
}