package pac.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by macbookair on 28.03.16.
 */

@Entity
@Table(name = "Account")
//@NamedEntityGraph(name = "Account.pricePositions",
//        attributeNodes = @NamedAttributeNode("pricePositions"))
public class Account {

    @Id
    @Column(name = "login")
    private String login;
    @Column(name = "pass")
    private String pass;
    @Column(name = "email")
    private String email;
    @Column(name = "telNumber")
    private String telNumber;
    @Column(name = "state")
    private boolean state;
    @Column(name = "photoAccount")
    private String photoAccount;
    @Column(name = "photoBackground1")
    private String photoBackground1;
    @Column(name = "photoBackground2")
    private String photoBackground2;
    @Column(name = "photoBackground3")
    private String photoBackground3;


    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PositionOfPrice> pricePositions  = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "accountType_typeName")
    private AccountType accountType;

    @OneToMany(mappedBy = "accountCustomer", fetch = FetchType.EAGER,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookingSet = new HashSet<>();
//    private List<Booking> bookingList = new ArrayList<>();

    public Account() {

    }

    public Account(String telNumber, String email, String pass, String login, AccountType accountType) {
        this.accountType = accountType;
        this.telNumber = telNumber;
        this.email = email;
        this.pass = pass;
        this.login = login;
    }

    @Override
    public int hashCode(){
        int logInt = login.hashCode();
        int passInt = pass.hashCode();
        int hashCode = logInt * passInt * 13 * 11 ;
        int two32 = 2 << 31 ;
        return  two32 - hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


//    public void deleteBooking(Booking booking){
//        bookingList.remove(booking);
//    }
//    public void deleteAllBooking(){
//        bookingList.clear();
//    }
//    public void addBookingList(Booking booking) {
//        if (!bookingList.contains(booking)) {
//            bookingList.add(booking);
//            if (booking.getAccountCustomer() != null){
//                booking.getAccountCustomer().bookingList.remove(booking);
//            }
//            booking.setAccountCustomer(this);
//        }
//    }
//
//    public List<Booking> getBookingSet() {
//        return bookingList;
//    }

    public void deleteBooking(Booking booking){
        if (bookingSet.contains(booking)) {
            bookingSet.remove(booking);
        }
    }
    public void deleteAllBooking(){
        bookingSet.clear();
    }
    public void addBookingList(Booking booking) {
        if (!bookingSet.contains(booking)) {
            bookingSet.add(booking);
            if (booking.getAccountCustomer() != null){
                booking.getAccountCustomer().bookingSet.remove(booking);
            }
            booking.setAccountCustomer(this);
        }
    }

    @JsonManagedReference
    public Set<Booking> getBookingSet() {
        return bookingSet;
    }
    @JsonManagedReference
    public Set<PositionOfPrice> getPricePositions() {
        return pricePositions;
    }

    public void deletePricePosition(PositionOfPrice positionOfPrice){
        if (pricePositions.contains(positionOfPrice)){
            pricePositions.remove(positionOfPrice);
        }
    }

    public void addPricePositions(PositionOfPrice position){

        pricePositions.add(position);
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getPhotoAccount() {
        return photoAccount;
    }

    public void setPhotoAccount(String photoAccount) {
        this.photoAccount = photoAccount;
    }

    public String getPhotoBackground1() {
        return photoBackground1;
    }

    public void setPhotoBackground1(String photoBackground1) {
        this.photoBackground1 = photoBackground1;
    }

    public String getPhotoBackground2() {
        return photoBackground2;
    }

    public void setPhotoBackground2(String photoBackground2) {
        this.photoBackground2 = photoBackground2;
    }

    public String getPhotoBackground3() {
        return photoBackground3;
    }

    public void setPhotoBackground3(String photoBackground3) {
        this.photoBackground3 = photoBackground3;
    }
}
