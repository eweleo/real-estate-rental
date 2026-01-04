package com.example.application.services;

import com.example.application.entity.CardBrand;
import com.example.application.entity.PaymentMethod;
import com.example.application.entity.User;
import com.example.application.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional(readOnly = true)
    public List<PaymentMethod> findByUser(User user) {
        return paymentMethodRepository.findByUserIdOrderByIsDefaultDescAddedDateDesc(user.getId());
    }

    @Transactional
    public PaymentMethod addCreditCard(User user, String cardNumber, String cardHolderName,
                                       String expiryMonth, String expiryYear, String cvv,
                                       String billingAddress,
                                       String billingCity, String billingZipCode, String billingCountry) {

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setUser(user);
        paymentMethod.setPaymentType(com.example.application.entity.PaymentType.CARD);
        paymentMethod.setCardHolderName(cardHolderName);

        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        paymentMethod.setCardLastFourDigits(lastFour);

        CardBrand brand = CardBrand.detectFromNumber(cardNumber);
        paymentMethod.setCardBrand(brand);

        paymentMethod.setExpiryMonth(expiryMonth);
        paymentMethod.setExpiryYear(expiryYear);
        paymentMethod.setAddedDate(LocalDateTime.now());

        paymentMethod.setPaymentToken("pm_" + UUID.randomUUID().toString());

        List<PaymentMethod> existing = findByUser(user);
        if (existing.isEmpty()) {
            paymentMethod.setDefault(true);
        }

        return paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public void setDefaultPaymentMethod(Long paymentMethodId, User user) {
        List<PaymentMethod> userMethods = findByUser(user);
        userMethods.forEach(pm -> pm.setDefault(false));
        paymentMethodRepository.saveAll(userMethods);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Metoda płatności nie znaleziona"));

        if (!paymentMethod.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Brak uprawnień");
        }

        paymentMethod.setDefault(true);
        paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public void deletePaymentMethod(Long paymentMethodId, User user) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Metoda płatności nie znaleziona"));

        if (!paymentMethod.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Brak uprawnień");
        }

        boolean wasDefault = paymentMethod.isDefault();
        paymentMethodRepository.delete(paymentMethod);

        if (wasDefault) {
            List<PaymentMethod> remaining = findByUser(user);
            if (!remaining.isEmpty()) {
                remaining.get(0).setDefault(true);
                paymentMethodRepository.save(remaining.get(0));
            }
        }
    }

    @Transactional
    public Optional<PaymentMethod> getDefaultPaymentMethod(User user){
        return paymentMethodRepository.findByUserIdAndIsDefaultTrue(user.getId());
    }

}
