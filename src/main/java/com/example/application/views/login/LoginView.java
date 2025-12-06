package com.example.application.views.login;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@AnonymousAllowed
@PageTitle("Logowanie")
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private final AuthenticationManager authenticationManager;

    private EmailField email;
    private PasswordField password;
    private Button loginButton;

    public LoginView(AuthenticatedUser authenticatedUser, AuthenticationManager authenticationManager) {
        this.authenticatedUser = authenticatedUser;
        this.authenticationManager = authenticationManager;

        setSizeFull();
        getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)");

        add(createLoginForm());
    }

    private VerticalLayout createLoginForm() {
        VerticalLayout loginForm = new VerticalLayout();
        loginForm.setWidth("400px");
        loginForm.setPadding(true);
        loginForm.setSpacing(true);
        loginForm.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 10px 40px rgba(0,0,0,0.2)");

        H1 appTitle = new H1("Nieruchomości");
        appTitle.getStyle()
                .set("margin", "0")
                .set("text-align", "center")
                .set("color", "#667eea")
                .set("font-size", "32px");

        H2 loginTitle = new H2("Zaloguj się");
        loginTitle.getStyle()
                .set("margin", "10px 0 20px 0")
                .set("text-align", "center")
                .set("color", "#333")
                .set("font-weight", "500")
                .set("font-size", "20px");

        email = new EmailField("Email");
        email.setWidth("100%");
        email.setRequired(true);
        email.getStyle().set("margin-bottom", "10px");

        password = new PasswordField("Hasło");
        password.setWidth("100%");
        password.setRequired(true);
        password.getStyle().set("margin-bottom", "20px");

        // Dodaj obsługę Enter
        password.addKeyPressListener(event -> {
            if (event.getKey().getKeys().contains("Enter")) {
                handleLogin();
            }
        });

        loginButton = new Button("Zaloguj się", e -> handleLogin());
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.setWidth("100%");
        loginButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none")
                .set("margin-bottom", "10px");

        Div registerSection = new Div();
        registerSection.getStyle()
                .set("text-align", "center")
                .set("padding-top", "20px")
                .set("border-top", "1px solid #e0e0e0");

        Paragraph registerText = new Paragraph("Nie masz jeszcze konta?");
        registerText.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#666");

        RouterLink registerLink = new RouterLink("Zarejestruj się", RegisterView.class);
        registerLink.getStyle()
                .set("color", "#667eea")
                .set("text-decoration", "none")
                .set("font-weight", "600")
                .set("font-size", "16px");

        registerSection.add(registerText, registerLink);

        loginForm.add(appTitle, loginTitle, email, password, loginButton, registerSection);

        return loginForm;
    }

    private void handleLogin() {
        try {
            String emailValue = email.getValue();
            String passwordValue = password.getValue();

            if (emailValue.isEmpty() || passwordValue.isEmpty()) {
                showNotification("Wypełnij wszystkie pola", NotificationVariant.LUMO_ERROR);
                return;
            }

            System.out.println("Próba logowania dla: " + emailValue);

            // Autentykacja
            UsernamePasswordAuthenticationToken authReq =
                    new UsernamePasswordAuthenticationToken(emailValue, passwordValue);
            Authentication auth = authenticationManager.authenticate(authReq);

            System.out.println("Autentykacja udana: " + auth.isAuthenticated());

            // Ustaw kontekst bezpieczeństwa
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(auth);
            SecurityContextHolder.setContext(securityContext);

            // Pobierz request i response
            VaadinServletRequest vaadinRequest = VaadinServletRequest.getCurrent();
            VaadinServletResponse vaadinResponse = VaadinServletResponse.getCurrent();

            if (vaadinRequest != null && vaadinResponse != null) {
                HttpServletRequest request = vaadinRequest.getHttpServletRequest();
                HttpServletResponse response = vaadinResponse.getHttpServletResponse();

                // Zapisz kontekst w sesji
                request.getSession().setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        securityContext
                );

                System.out.println("Kontekst zapisany w sesji: " + request.getSession().getId());
            }

            showNotification("Zalogowano pomyślnie!", NotificationVariant.LUMO_SUCCESS);

            // Nawiguj do strony głównej z opóźnieniem
            UI.getCurrent().access(() -> {
                UI.getCurrent().navigate("");
                UI.getCurrent().getPage().reload();
            });

        } catch (AuthenticationException e) {
            System.err.println("Błąd autentykacji: " + e.getMessage());
            showNotification("Nieprawidłowy email lub hasło", NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Nieoczekiwany błąd: " + e.getMessage());
            showNotification("Wystąpił błąd podczas logowania", NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            System.out.println("Użytkownik już zalogowany, przekierowanie...");
            event.forwardTo("");
        }
    }
}