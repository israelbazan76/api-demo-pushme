package com.bazan.demopushme.ui;

import com.bazan.demopushme.dto.PushNotificationResponse;
import com.bazan.demopushme.repository.DeviceTokenRepository;
import com.bazan.demopushme.service.FcmRestService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Vista de Vaadin para enviar notificaciones push a todos los dispositivos.
 * Esta clase sirve como la capa de frontend para interactuar con el PushNotificationService.
 * La anotación @Route hace que esta clase sea accesible desde la URL raíz de la aplicación.
 */
@Route("notifications")
public class NotificationView extends VerticalLayout {

    // Se inyecta automáticamente la dependencia del servicio de notificaciones.
    private final FcmRestService pushNotificationService;

    // Se inyecta automáticamente la dependencia del repositorio de dispositivos.
    private final DeviceTokenRepository deviceTokenRepository;

    // Componentes de la UI
    private final TextField titleField = new TextField("Título de la Notificación");
    private final TextField bodyField = new TextField("Cuerpo del Mensaje");
    private final Button sendButton = new Button("Enviar a Todos");
    private final Grid<PushNotificationResponse> responseGrid = new Grid<>(PushNotificationResponse.class);

    // Constructor que recibe las dependencias inyectadas por Spring.
    @Autowired
    public NotificationView(FcmRestService pushNotificationService, DeviceTokenRepository deviceTokenRepository) {
        this.pushNotificationService = pushNotificationService;
        this.deviceTokenRepository = deviceTokenRepository;
        setupUI();
        setupListeners();
    }

    /**
     * Configura el layout y añade los componentes a la vista.
     */
    private void setupUI() {
        // Título de la vista
        add(new H1("Enviar Notificaciones Push"));

        // Diseño del formulario con los campos de texto
        FormLayout formLayout = new FormLayout();
        formLayout.add(titleField, bodyField);
        titleField.setRequired(true);
        bodyField.setRequired(true);

        // Añadir el formulario y el botón a la vista principal
        add(formLayout, sendButton);

        // Configurar la cuadrícula (Grid) para mostrar los resultados.
          // Configurar la cuadrícula (Grid) para mostrar los resultados.
        responseGrid.setColumns("success", "messageId", "error", "retries");
        add(responseGrid);

        // Estilos para centrar y dar espaciado
        setAlignItems(Alignment.CENTER);
        setSpacing(true);
    }

    /**
     * Configura los oyentes de eventos, como el del botón de envío.
     */
    private void setupListeners() {
        sendButton.addClickListener(event -> {
            // Validar que los campos de texto no estén vacíos.
            if (titleField.isEmpty() || bodyField.isEmpty()) {
                Notification.show("Por favor, complete todos los campos.", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Llamar al servicio para enviar las notificaciones.
            String title = titleField.getValue();
            String body = bodyField.getValue();

            // Usar un hilo separado para la llamada al servicio para evitar bloquear la UI
            // Esto es crucial para operaciones de red que pueden tardar.
            new Thread(() -> {
                try {
                    List<PushNotificationResponse> responses = pushNotificationService.sendPushNotificationsToAll(title, body);
                    // Actualizar la UI con los resultados.
                    getUI().ifPresent(ui -> ui.access(() -> {
                        responseGrid.setItems(responses);
                        Notification.show("Notificaciones enviadas. Revise los resultados en la tabla.", 3000, Notification.Position.MIDDLE);
                    }));
                } catch (Exception e) {
                    // Manejar cualquier error inesperado
                    getUI().ifPresent(ui -> ui.access(() -> {
                        Notification.show("Ocurrió un error al enviar las notificaciones: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                        System.err.println("Error en el servicio: " + e.getMessage());
                    }));
                }
            }).start();
        });
    }
}

