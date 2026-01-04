package com.example.application.views.account;

import com.example.application.entity.Address;
import com.example.application.entity.Company;
import com.example.application.entity.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;

public class AccountForm extends VerticalLayout {

    private final Binder<User> userBinder;
    private final Binder<Company> companyBinder;
    private final Binder<Address> addressBinder;
    private final Runnable onSave;
    private final boolean isLandlord;

    private TextField firstName;
    private TextField lastName;
    private EmailField email;
    private TextField phoneNumber;
    private TextField street;
    private TextField city;
    private TextField zipCode;
    private TextField country;

    private TextField companyName;
    private TextField nip;

    public AccountForm(User user, Runnable onSave) {
        this.userBinder = new Binder<>(User.class);
        this.companyBinder = new Binder<>(Company.class);
        this.addressBinder = new Binder<>(Address.class);
        this.onSave = onSave;
        this.isLandlord = user.isLandlord();

        configureForm();
        configureUserBinder();
        configureAddressBinder();

        if (isLandlord) {
            configureCompanyBinder();
            if (user.getCompany() != null) {
                companyBinder.readBean(user.getCompany());
            } else {
                companyBinder.readBean(new Company());
            }
        }

        userBinder.readBean(user);

        if (user.getAddress() != null) {
            addressBinder.readBean(user.getAddress());
        } else {
            addressBinder.readBean(new Address());
        }
    }

    private void configureForm() {
        setSpacing(true);
        setPadding(true);
        setWidth("100%");
        getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        H3 sectionTitle = new H3(isLandlord ? "Dane osobowe i firmowe" : "Dane osobowe");
        sectionTitle.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#667eea");

        firstName = new TextField("Imię");
        firstName.setRequired(true);
        firstName.setWidth("100%");

        lastName = new TextField("Nazwisko");
        lastName.setRequired(true);
        lastName.setWidth("100%");

        email = new EmailField("E-mail");
        email.setRequired(true);
        email.setWidth("100%");

        phoneNumber = new TextField("Numer telefonu");
        phoneNumber.setRequired(true);
        phoneNumber.setWidth("100%");
        phoneNumber.setPlaceholder("123456789");

        add(sectionTitle, firstName, lastName, email, phoneNumber);

        if (isLandlord) {
            H3 companySection = new H3("Dane firmy");
            companySection.getStyle()
                    .set("margin", "20px 0 10px 0")
                    .set("color", "#667eea");

            companyName = new TextField("Nazwa firmy");
            companyName.setRequired(true);
            companyName.setWidth("100%");
            companyName.setPlaceholder("Wprowadź nazwę firmy");

            nip = new TextField("NIP");
            nip.setRequired(true);
            nip.setWidth("100%");
            nip.setPlaceholder("1234567890");
            nip.setHelperText("10 cyfr bez kresek");
            nip.setMaxLength(10);

            add(companySection, companyName, nip);
        }

        H3 addressSection = new H3("Adres");
        addressSection.getStyle()
                .set("margin", "20px 0 10px 0")
                .set("color", "#667eea");

        street = new TextField("Ulica i numer");
        street.setWidth("100%");
        street.setPlaceholder("ul. Marszałkowska 45/47, m. 12");
        street.setHelperText("Pełny adres z ulicą, numerem budynku i mieszkania");

        city = new TextField("Miasto");
        city.setWidth("100%");
        city.setRequired(true);

        zipCode = new TextField("Kod pocztowy");
        zipCode.setWidth("100%");
        zipCode.setPlaceholder("00-000");
        zipCode.setRequired(true);

        country = new TextField("Kraj");
        country.setWidth("100%");
        country.setValue("Polska");

        HorizontalLayout cityLayout = new HorizontalLayout(city, zipCode);
        cityLayout.setWidth("100%");
        cityLayout.getStyle().set("gap", "15px");

        add(addressSection, street, cityLayout, country);

        Button saveButton = new Button("Zapisz zmiany", e -> onSave.run());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("margin-top", "20px")
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");

        add(saveButton);
    }

    private void configureUserBinder() {
        userBinder.forField(firstName)
                .asRequired("Imię jest wymagane")
                .withValidator(name -> name.length() >= 2, "Imię musi mieć co najmniej 2 znaki")
                .withValidator(name -> name.length() <= 50, "Imię nie może być dłuższe niż 50 znaków")
                .bind(User::getFirstName, User::setFirstName);

        userBinder.forField(lastName)
                .asRequired("Nazwisko jest wymagane")
                .withValidator(name -> name.length() >= 2, "Nazwisko musi mieć co najmniej 2 znaki")
                .withValidator(name -> name.length() <= 50, "Nazwisko nie może być dłuższe niż 50 znaków")
                .bind(User::getLastName, User::setLastName);

        userBinder.forField(email)
                .asRequired("Email jest wymagany")
                .withValidator(new EmailValidator("Nieprawidłowy adres email"))
                .bind(User::getEmail, User::setEmail);

        userBinder.forField(phoneNumber)
                .asRequired("Numer telefonu jest wymagany")
                .withValidator(phone -> phone.matches("\\d{9}"), "Numer telefonu musi składać się z 9 cyfr")
                .bind(User::getTelephoneNumber, User::setTelephoneNumber);
    }

    private void configureAddressBinder() {
        addressBinder.forField(street)
                .bind(Address::getStreet, Address::setStreet);

        addressBinder.forField(city)
                .asRequired("Miasto jest wymagane")
                .bind(Address::getCity, Address::setCity);

        addressBinder.forField(zipCode)
                .asRequired("Kod pocztowy jest wymagany")
                .withValidator(zip -> zip.matches("\\d{2}-\\d{3}"),
                        "Kod pocztowy musi być w formacie XX-XXX")
                .bind(Address::getZipCode, Address::setZipCode);

        addressBinder.forField(country)
                .bind(Address::getCountry, Address::setCountry);
    }

    private void configureCompanyBinder() {
        companyBinder.forField(companyName)
                .asRequired("Nazwa firmy jest wymagana")
                .withValidator(name -> name.length() >= 2, "Nazwa firmy musi mieć co najmniej 2 znaki")
                .withValidator(name -> name.length() <= 100, "Nazwa firmy nie może być dłuższa niż 100 znaków")
                .bind(Company::getCompanyName, Company::setCompanyName);

        companyBinder.forField(nip)
                .asRequired("NIP jest wymagany")
                .withValidator(n -> n.matches("\\d{10}"), "NIP musi składać się z 10 cyfr")
                .bind(Company::getNip, Company::setNip);
    }

    public void saveToUser(User user) throws ValidationException {
        userBinder.writeBean(user);

        Address address = user.getAddress();
        if (address == null) {
            address = new Address();
            user.setAddress(address);
        }
        addressBinder.writeBean(address);
    }

    public void saveToCompany(Company company) throws ValidationException {
        if (isLandlord && companyBinder != null) {
            companyBinder.writeBean(company);
        }
    }

    public String getEmail() {
        return email.getValue();
    }

    public String getNip() {
        return isLandlord && nip != null ? nip.getValue() : null;
    }

    public boolean isValid() {
        boolean userValid = userBinder.isValid();
        boolean addressValid = addressBinder.isValid();
        boolean companyValid = !isLandlord || companyBinder.isValid();
        return userValid && addressValid && companyValid;
    }
}