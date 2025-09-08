package com.bazan.demopushme.ui;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Vista de Vaadin para enviar notificaciones push a todos los dispositivos.
 * Esta clase sirve como la capa de frontend para interactuar con el PushNotificationService.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Enviar Notificaciones")
public class NotificationView extends VerticalLayout {

    // Se inyecta automáticamente la dependencia del servicio de notificaciones.
    private final FcmRestService pushNotificationService;

    // Se inyecta automáticamente la dependencia del repositorio de dispositivos.
    private final DeviceTokenRepository deviceTokenRepository;

    // Componentes de la UI
    private final TextField titleField = new TextField("Título de la Notificación");
    private final TextField bodyField = new TextField("Cuerpo del Mensaje");
    private final Button sendButton = new Button("Enviar a Todos");
    private final Button refreshButton = new Button("Actualizar Grilla");
    private final Grid<PushNotificationResponse> responseGrid = new Grid<>(PushNotificationResponse.class);
    // Proveedor de datos reactivo para la grilla
    private final ListDataProvider<PushNotificationResponse> dataProvider = new ListDataProvider<>(new ArrayList<>());


    // Pool de hilos para ejecutar tareas en segundo plano. Esto es más robusto que usar new Thread().
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
        H1 pageTitle = new H1("Enviar Notificaciones Push");
        pageTitle.addClassName("page-title");
        add(pageTitle);

        // Diseño del formulario con los campos de texto
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("form-container");
        formLayout.add(titleField, bodyField);
        titleField.setRequired(true);
        bodyField.setRequired(true);

        // Añadir el formulario, el botón y el indicador a la vista principal
        sendButton.addClassName("send-button");
        add(formLayout, sendButton, refreshButton);

        // Configurar la cuadrícula (Grid) para mostrar los resultados.
        responseGrid.addClassName("response-grid");
        responseGrid.setColumns("success", "messageId", "error", "retries");
        responseGrid.addColumn(response -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            return response.getTimestamp().format(formatter);
        }).setHeader("Fecha y Hora").setKey("timestamp").setSortable(true);

        responseGrid.setItems(dataProvider);
        add(responseGrid);

        // Estilos para centrar y dar espaciado
        addClassName("main-layout");
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

            // Cambiar texto del botón y deshabilitarlo
            sendButton.setText("Enviando...");
            sendButton.setEnabled(false);

            // Enviar la tarea al pool de hilos para su ejecución.
            executorService.submit(() -> {
                System.out.println("Iniciando el envío de notificaciones...");
                List<PushNotificationResponse> responses = null;
                Exception error = null;
                try {
                    String title = titleField.getValue();
                    String body = bodyField.getValue();
                    responses = pushNotificationService.sendPushNotificationsToAll(title, body);
                    System.out.println("Llamada al servicio de notificaciones exitosa.");
                } catch (Exception e) {
                    System.err.println("Ocurrió un error en el servicio: " + e.getMessage());
                    error = e;
                } finally {
                    System.out.println("El bloque 'finally' se ha ejecutado. Restaurando la UI...");
                    // Restaurar el texto y el estado del botón, y ocultar el indicador de carga
                    List<PushNotificationResponse> finalResponses = responses;
                    Exception finalError = error;
                    getUI().ifPresent(ui -> ui.access(() -> {
                        sendButton.setText("Enviar a Todos");
                        sendButton.setEnabled(true);
                        if (finalError == null) {
                            dataProvider.getItems().addAll(finalResponses);
                            Notification.show("Notificaciones enviadas. Revise los resultados en la tabla.", 3000, Notification.Position.MIDDLE);
                        } else {
                            dataProvider.getItems().addAll(new ArrayList<>());
                            Notification.show("Ocurrió un error al enviar las notificaciones: " + finalError.getMessage(), 5000, Notification.Position.MIDDLE);
                        }
                    }));
                }
            });
        });
        refreshButton.addClickListener(event -> {
            dataProvider.refreshAll();
            Notification.show("Grilla actualizada manualmente.", 3000, Notification.Position.MIDDLE);
        });
    }
}
