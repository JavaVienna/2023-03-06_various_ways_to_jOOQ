package at.softwarecraftsmen.persistenceshowcase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor

@Entity
@Table(name = "photographers")
public class Photographer extends AbstractPerson {

    @AttributeOverride(name = "streetNumber", column = @Column(name = "studio_street_number", length = 64))
    @AttributeOverride(name = "zipCode", column = @Column(name = "studio_zip_code", length = 16))
    @AttributeOverride(name = "city", column = @Column(name = "studio_city", length = 64))
    @AssociationOverride(
        name = "country",
        joinColumns = @JoinColumn(name = "studio_country_id"),
        foreignKey = @ForeignKey(name = "fk_photographers_studio_country")
    )
    private Address studioAddress;

    @NotNull
    @AttributeOverride(name = "streetNumber", column = @Column(name = "billing_street_number", length = 64, nullable = false))
    @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip_code", length = 16, nullable = false))
    @AttributeOverride(name = "city", column = @Column(name = "billing_city", length = 64, nullable = false))
    @AssociationOverride(
        name = "country",
        joinColumns = @JoinColumn(name = "billing_country_id", nullable = false),
        foreignKey = @ForeignKey(name = "fk_photographers_billing_country")
    )
    private Address billingAddress;

    @NotNull
    @AttributeOverride(name = "countryCode", column = @Column(name = "mobile_country_code", nullable = false, columnDefinition = "numeric(3) check (mobile_country_code>=0 AND mobile_country_code<=999)"))
    @AttributeOverride(name = "areaCode", column = @Column(name = "mobile_area_code", nullable = false, columnDefinition = "numeric(4) check (mobile_area_code>=0 AND mobile_area_code<=9999)"))
    @AttributeOverride(name = "serialNumber", column = @Column(name = "mobile_serial_number", length = 16, nullable = false))
    private PhoneNumber mobileNumber;

    @NotNull
    @AttributeOverride(name = "countryCode", column = @Column(name = "business_country_code", columnDefinition = "numeric(3) check (business_country_code>=0 AND business_country_code<=999)"))
    @AttributeOverride(name = "areaCode", column = @Column(name = "business_area_code", columnDefinition = "numeric(4) check (business_area_code>=0 AND business_area_code<=9999)"))
    @AttributeOverride(name = "serialNumber", column = @Column(name = "business_serial_number", length = 16))
    private PhoneNumber businessNumber;

    @ElementCollection
    @CollectionTable(
        name = "photographer_emails",
        joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "fk_photographer_emails"))
    )
    private List<EmailAddress> emails = new ArrayList<>(2);

    @Builder
    public Photographer(@Email @NotNull String userName, @NotNull String firstName, @NotNull String lastName, Address studioAddress,
                        Address billingAddress, PhoneNumber mobileNumber, PhoneNumber businessNumber, List<EmailAddress> emails) {
        super(userName, firstName, lastName);
        this.studioAddress = studioAddress;
        this.billingAddress = billingAddress;
        this.mobileNumber = mobileNumber;
        this.businessNumber = businessNumber;
        this.emails = new ArrayList<>(emails);
    }
}
