package com.bazan.demopushme.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
/**
 * Clase de configuración para la aplicación Vaadin.
 * Esta clase configura el tema de Vaadin para toda la aplicación.
 * Debe tener la anotación @Theme y extender la interfaz AppShellConfigurator.
 */
@Theme(value = "app-theme")
@Push
public class VaadinAppConfig implements AppShellConfigurator {
}

