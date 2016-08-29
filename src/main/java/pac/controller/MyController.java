package pac.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import pac.entities.*;
import pac.errors.Message;
import pac.services.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Date;
import java.util.*;

@Controller
@RequestMapping("/")
public class MyController {
//    static final int DEFAULT_GROUP_ID = -1;

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountTypeService accountTypeService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PositionOfPriceService positionOfPriceService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingPositionService bookingPositionService;
    private String PATH_TO_IMG = "/var/lib/openshift/57728e217628e1ec270000ea/app-root/data/img/";
//  /Users/macbookair/IdeaProjects/App/src/main/resources/

    //    /var/lib/openshift/57728e217628e1ec270000ea/app-root/data/img/
    //        /var/lib/openshift/PROJECT_ID/app-root/data/
    private String IMAGE_EXTENSION = ".png";


    @RequestMapping(value = "/bookAjax", method = RequestMethod.POST)
    public
    @ResponseBody
    Product bookAjax(@RequestParam String positionID, @RequestParam Integer capacity, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String clientLogin = auth.getName();
        Account client = accountService.findAccount(clientLogin);
        PositionOfPrice position = positionOfPriceService.findPosition(Integer.valueOf(positionID));
        String s = position.getAccount().getLogin();
        Account customer = accountService.findAccount(s);
        Product product = position.getProduct();

        Booking booking = bookingService.findForClient(client, customer);
        if (booking == null) {
            booking = new Booking(client, customer); //, customer
            bookingService.save(booking);
//            System.out.println(booking.getId() + " это ID заказа ");
            BookingPosition bookingPosition = new BookingPosition(capacity, booking, product); //, booking
            bookingPositionService.setBookingPosition(bookingPosition);
            accountService.updateAccount(customer);
        } else {
//            System.out.println(booking.getId() + " это ID заказа else");
            BookingPosition bookingPosition = new BookingPosition(capacity, booking, product); // booking ,
            bookingPositionService.setBookingPosition(bookingPosition);
            bookingService.updateBooking(booking);
        }
        return product;
    }

    @RequestMapping(value = "/confirmBookAjax", method = RequestMethod.POST)
    public @ResponseBody Booking confirmBookAjax(@RequestParam String positionID, @RequestParam Integer capacity, HttpServletResponse response) throws IOException {

        BookingPosition bookingPosition = bookingPositionService.findByID(Integer.valueOf(positionID));
        Account customer = bookingPosition.getBooking().getAccountCustomer();
        Booking booking = bookingPosition.getBooking();
        int posCapacity = bookingPosition.getCapacity();
        Product product = bookingPosition.getProduct();

        if (product != null && product.getId() != 0) {
            int amount = product.getAmount();
            if (amount >= capacity && capacity >= 0) {
                amount -= capacity;
                product.setAmount(amount);
                productService.setProduct(product);
                if (posCapacity <= capacity) {
                    booking.deleteBookingPosition(bookingPosition);
                    bookingService.updateBooking(booking);

                    if (booking.getBookingPositions() == null || booking.getBookingPositions().size() == 0) {
                        customer.deleteBooking(booking);
                        accountService.updateAccount(customer);
                    } else {
                        System.out.println(booking.getBookingPositions().size() + "  лист не обновился");
                    }
                } else {
                    bookingPosition.setCapacity(posCapacity - capacity);
                    bookingPositionService.setBookingPosition(bookingPosition);
                }
            } else if (capacity >= 0) {
                capacity -= amount;
                product.setAmount(0);

                productService.setProduct(product);

                bookingPosition.setCapacity(capacity);
                bookingPositionService.setBookingPosition(bookingPosition);
            }
        } else {
            if (capacity >= posCapacity) {

                booking.deleteBookingPosition(bookingPosition);
                bookingService.updateBooking(booking);

                if (booking.getBookingPositions() == null || booking.getBookingPositions().size() == 0) {
                    customer.deleteBooking(booking);
                    accountService.updateAccount(customer);
                }

            } else if (capacity >= 0) {
//                System.out.println("тут 2");
                bookingPosition.setCapacity(posCapacity - capacity);
                bookingPositionService.setBookingPosition(bookingPosition);
            }
        }


        Set<Booking> set = accountService.findAccount(customer.getLogin()).getBookingSet();
        Booking booking1 = bookingService.findBooking(booking.getId());
//        List<Booking> list = new ArrayList<>(set);
//        Booking bookingResp = null;
        if (booking1 != null) {

//            System.out.println("alkjrvnlsbvlksnvksn");
            System.out.println(booking1.getId() +"  "+booking1.getAccountClient().getLogin()+"  " + booking1.getBookingPositions().size());
            for (BookingPosition b : booking1.getBookingPositions()) {
                System.out.println(b.getId() + "  ----   " + b.getProduct().getName());
            }
            return booking1;
        } else {
            response.sendError(404, "Not entity");
//            System.out.println("return null");
            return null;
        }

    }


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(HttpServletRequest request, Model model) {
        return "test";
    }

    @RequestMapping(value = "/sentest", method = RequestMethod.POST)
    public
    @ResponseBody
    Message sentest(@RequestParam Integer capacity) {

//        System.out.println(capacity + "   -----------");
//
////        File file1 = new File("/var/lib/openshift/57728e217628e1ec270000ea/app-root/data/");
//        File file2 = new File(PATH_TO_IMG, "test1.txt");
//        try{
//            FileWriter w = new FileWriter(file2);
//            w.write("Hello world");
//            w.flush();
//            w.close();
//        } catch (IOException e){
//            model.addAttribute("text1", e.getMessage() + "   write");
//        }
//////ssh 57728e217628e1ec270000ea@app-timoshdomain12.rhcloud.com
////
//        try {
//
//            File file1 = new File(PATH_TO_IMG, "test2.txt");
//
//            FileUtils.moveFile(file2, file1);
//
//            BufferedReader read = new BufferedReader(new FileReader(file1));
//
//            String str;
//            StringBuilder sb = new StringBuilder();
//
//            while((str = read.readLine()) != null){
//                sb.append(str);
//            }
//
//            model.addAttribute("text", sb.toString() + " foooooooo" );
//
//
//            System.out.println(file2.getAbsolutePath());
////            BufferedReader read1 = new BufferedReader(new FileReader(file2));
////
////            String str1;
////            StringBuilder sb1 = new StringBuilder();
////
////            while((str1 = read1.readLine()) != null){
////                sb1.append(str1);
////            }
////            model.addAttribute("text3", sb1.toString() + " foo111111" );
//
//
//
//        } catch (IOException e) {
//            model.addAttribute("text2", e.getMessage() + "  read");
//        }
//
//
//
//
        Message message = new Message(capacity.toString());
        System.out.println(message.getMsg());
        return message;
    }


    @RequestMapping("/")
    public String rootPage(Model model, HttpServletRequest request) {
        return "login";
    }

    @RequestMapping(value = "/home")
    public String home(HttpServletRequest request, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
//
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            String login = userDetail.getUsername();
            Account account = accountService.findAccount(login);

            if (account.getAccountType().getTypeName().equals("customer")) {
                List<PositionOfPrice> list = positionOfPriceService.listPositions(account);
                if (list.size() == 0) {
                    List<PositionOfPrice> list1 = new LinkedList<>();
                    Calendar c = Calendar.getInstance();
                    list1.add(new PositionOfPrice("Здесь будет ваши условия заказа", "Здесь будут ваши условия доставки",
                            new Date(c.YEAR, c.MONTH, c.DAY_OF_MONTH), 000000, account, new Product("Название товара",
                            "Описание товара", null, "Код модели", 000000)));
                    list = list1;
                }
//
                model.addAttribute("listPositions", list);
                model.addAttribute("account", account);
                return "canvas";
            } else if (account.getAccountType().getTypeName().equals("client")) {
                AccountType accountType = accountTypeService.findByTypeName("customer");
                List<Account> accountList = accountService.listAccount(accountType);
                model.addAttribute("accountList", accountList);
                return "home";
            } else return "login";
        } else return "login";

    }


    @RequestMapping(value = "/home/{login}")
    public String homePage(@PathVariable(value = "login") String login, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            Account account = accountService.findAccount(login);
            if (account != null && account.getAccountType().getTypeName().equals("customer")) {
                List<PositionOfPrice> list = positionOfPriceService.listPositions(account);
                if (list.size() == 0) {
                    List<PositionOfPrice> list1 = new LinkedList<>();
                    Calendar c = Calendar.getInstance();
                    list1.add(new PositionOfPrice("Здесь будет ваши условия заказа", "Здесь будут ваши условия доставки",
                            new Date(c.YEAR, c.MONTH, c.DAY_OF_MONTH), 000000, account, new Product("Название товара",
                            "Описание товара", null, "Код модели", 000000)));
                    list = list1;
                }
                model.addAttribute("listPositions", list);
                model.addAttribute("login", login);
                model.addAttribute("account", account);
                return "canvas";
            } else return "login";
        } else return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout, HttpServletRequest request) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
        }
        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }
        model.setViewName("login");
        return model;
    }

    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);

        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Неверный логин или пароль";
        } else if (exception instanceof LockedException) {
            error = exception.getMessage();
        } else {
            error = "Неверный логин или пароль";
        }
        return error;
    }

    @RequestMapping(value = "/registrat")
    public String registratPage() {
        return "registrat";
    }

    @RequestMapping(value = "/logout")
    public String logoutPage(Model model) {
//        model.addAttribute("error", )
        return "login";
    }

    @RequestMapping(value = "/addPricePosition", method = RequestMethod.GET)
    public String getNewPosition(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "customer";
        } else return "login";


    }


    @RequestMapping(value = "/addPricePosition", method = RequestMethod.POST)
    public String addPricePosition(@RequestParam String name, @RequestParam String codeOfModel,
                                   @RequestParam String description, @RequestParam MultipartFile photo,
                                   @RequestParam int capacity, @RequestParam String bookingCondition,
                                   @RequestParam String deliveryCondition, @RequestParam double cost, HttpServletRequest request, Model model) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            String login = userDetail.getUsername();
            Account account = accountService.findAccount(login);

            String ref = name + codeOfModel + login; // IMAGE_EXTENSION
//57728e217628e1ec270000ea%40app-timoshdomain12.rhcloud.com/Users/macbookair/IdeaProjects/App/src/main/webapp
//        String relativepath = "/img/";
//        String absolutePath = request.getRealPath(relativepath);
//        String path = absolutePath+"/"+ref;
//            String relativeWebPath = "/img/";
//            try {
//                URL url = request.getSession().getServletContext().getResource("/img/");
//                d = url.getPath();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            String filename = photo.getOriginalFilename();
//            String path = request.getServletContext().getRealPath("/");
//            String path = request.getRealPath("/");
            Product product = productService.findProduct(name, codeOfModel, ref);    //////////////////////////  вот тут

            if (product != null) {
                model.addAttribute("error", "Название: " + name + ", модель: " + codeOfModel + " -- тот продукт уже существует," +
                        " попробуйте добавить что-то другое");
                return "customer";
            } else {
                if (!photo.isEmpty()) {
                    File file = new File(PATH_TO_IMG);
                    if (!(file.exists())) {
                        file.mkdirs();
                    }

                    File file1 = new File(file, ref + IMAGE_EXTENSION);
                    try {
                        FileOutputStream fileOut = new FileOutputStream(file1);
                        fileOut.write(photo.getBytes());
                        fileOut.flush();
                        fileOut.close();
                    } catch (IOException e) {
                        model.addAttribute("error", e.getMessage());
                    }

                    System.out.println("------------original path ---------  " + file.getAbsolutePath() + "        ----------");

                    product = new Product(name, description, ref, codeOfModel, capacity);

                } else product = new Product(name, description, null, codeOfModel, capacity);

            }

            Calendar c = Calendar.getInstance();
            PositionOfPrice positionOfPrice = new PositionOfPrice(bookingCondition, deliveryCondition,
                    new Date(c.YEAR, c.MONTH, c.DAY_OF_MONTH), cost, account, product);
            account.addPricePositions(positionOfPrice);
            accountService.updateAccount(account);
//                                                      проверить будет ли оно правильно работать добавле поз после ее создания
            List<PositionOfPrice> listPosition = accountService.listPositions(account);

            if (listPosition.size() != 0) {
                model.addAttribute("listPositions", listPosition);
            } else {
                System.out.println("Лист позиций пуст");
                model.addAttribute("error", "Проблеммы у сервера в addPricePosition");
                return "customer";
            }
            model.addAttribute("account", account);

            return "canvas";
        } else return "login";
    }


    @RequestMapping(value = "/ownData/{login}")
    public String ownData(@PathVariable(value = "login") String login, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            String userName = auth.getName();
            if (login != null) {
                Account account1 = accountService.findAccount(userName);
                Account account = accountService.findAccount(login);
//                if (account != null && account.getAccountType().getTypeName().equals("customer") && account1.getAccountType().getTypeName().equals("client")) {
                if (account != null && account1.getAccountType().getTypeName().equals("client")) {
                    String refPhoto = account.getPhotoAccount();
                    if ((new File(PATH_TO_IMG, refPhoto + IMAGE_EXTENSION)).exists()) { //login
                        // существует
                        model.addAttribute("refPhoto", refPhoto); //IMAGE_EXTENSION
                        model.addAttribute("login", login);
                    } else {
                        // не существует
                        model.addAttribute("refPhoto", null);
                        model.addAttribute("login", login);
                    }

                    model.addAttribute("email", account.getEmail());
                    model.addAttribute("telNumber", account.getTelNumber());

                    return "ownData";
                } else return "login";
            }
            return "login";
        } else return "login";
    }

    @RequestMapping(value = "/ownData")
    public String ownData(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String login = auth.getName();
            Account account = accountService.findAccount(login);
//            System.out.println(account.getAccountType().getTypeName());
//            if (account.getAccountType().getTypeName().equals("customer") | account.getAccountType().getTypeName().equals("client")) {
            String refPhoto = account.getPhotoAccount();
            if ((new File(PATH_TO_IMG, refPhoto + IMAGE_EXTENSION)).exists()) {
                // существует
                model.addAttribute("refPhoto", refPhoto); //IMAGE_EXTENSION
            } else {
                // не существует
                model.addAttribute("refPhoto", null);
            }
            model.addAttribute("email", account.getEmail());
            model.addAttribute("telNumber", account.getTelNumber());

            if (account.getAccountType().getTypeName().equals("customer")) {
//                if ((new File(PATH_TO_IMG, refPhoto + IMAGE_EXTENSION)).exists()) {
//                    // существует
//                    model.addAttribute("refPhoto", refPhoto); //IMAGE_EXTENSION
//                } else {
//                    // не существует
//                    model.addAttribute("refPhoto", null);
//                }
////                System.out.println("ownData: " + account.getEmail() + "    " + account.getTelNumber());
//
//                model.addAttribute("email", account.getEmail());
//                model.addAttribute("telNumber", account.getTelNumber());
                return "ownData";
            } else if (account.getAccountType().getTypeName().equals("client")) {
                return "ownDataForClient";
            } else return "login";
        } else return "login";
    }

    @RequestMapping(value = "/ownDataForClient")
    public String ownDataForClient(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            String login = userDetail.getUsername();
            Account account = accountService.findAccount(login);
            if (account.getAccountType().getTypeName().equals("client")) {
                String refPhoto = account.getPhotoAccount();
                if ((new File(PATH_TO_IMG, refPhoto + IMAGE_EXTENSION)).exists()) {
                    // существует
                    model.addAttribute("refPhoto", refPhoto); //IMAGE_EXTENSION
                } else {
                    // не существует
                    model.addAttribute("refPhoto", null);
                }
                model.addAttribute("email", account.getEmail());
                model.addAttribute("telNumber", account.getTelNumber());
                return "ownDataForClient";
            } else return "login";
        } else return "login";
    }

    @RequestMapping(value = "/ownData", method = RequestMethod.POST)
    public String changeOwnData(@RequestParam MultipartFile photo, @RequestParam String email,
                                @RequestParam String telNumber, Model model) {


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String login = auth.getName();
            Account account = accountService.findAccount(login);

            String refPhoto = account.getPhotoAccount();
            if (!photo.isEmpty()) {
                if (refPhoto != null && refPhoto.length() > 0) {
                    File file = new File(PATH_TO_IMG, refPhoto + IMAGE_EXTENSION); // login
                    if (file.exists()) {
                        file.delete();
                    }
                } else {
                    refPhoto = account.getLogin();
                    account.setPhotoAccount(refPhoto);
//                        accountService.refreshAccount(account);
                }
                // существует

                File file1 = new File(PATH_TO_IMG, refPhoto + IMAGE_EXTENSION);  // login
                try (FileOutputStream fileOut = new FileOutputStream(file1)) {
                    fileOut.write(photo.getBytes());
                    fileOut.flush();
                    fileOut.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            model.addAttribute("refPhoto", refPhoto);   //login  IMAGE_EXTENSION c JSP страницы на сервер приходит путь без ".png"

            if (email.length() == 0) {
                model.addAttribute("email", account.getEmail());
//                System.out.println("changeOwnData: email by Account: " + account.getEmail());

            } else {
                account.setEmail(email);
//                System.out.println("changeOwnData: email: " + email);
                model.addAttribute("email", email);
            }

            if (telNumber.length() == 0) {
                model.addAttribute("telNumber", account.getTelNumber());
//                System.out.println("changeOwnData: telNumber by Account: " + account.getTelNumber());

            } else {
//                System.out.println("changeOwnData: telNumber:" + telNumber);
                account.setTelNumber(telNumber);
                model.addAttribute("telNumber", telNumber);
            }

            accountService.updateAccount(account);
            if (account.getAccountType().getTypeName().equals("customer")) {
                return "ownData";
            } else if (account.getAccountType().getTypeName().equals("client")) {
                return "ownDataForClient";
            } else return "login";
        } else return "login";
    }

    @RequestMapping(value = "/deletePosition/{positionID}", method = RequestMethod.GET)
    public String deletePosition(@PathVariable(value = "positionID") Integer positionID, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            PositionOfPrice positionOfPrice = positionOfPriceService.findPosition(positionID);
            if (positionOfPrice != null) {
                Account account = positionOfPrice.getAccount();
                Product product = positionOfPrice.getProduct();
                String refPhoto = product.getPhoto();

                File file = new File(PATH_TO_IMG, refPhoto + IMAGE_EXTENSION);
                if (file.exists()) {
                    file.delete();
                }

                account.deletePricePosition(positionOfPrice);
                accountService.updateAccount(account);


                Set<PositionOfPrice> setPositions = accountService.findAccount(account.getLogin()).getPricePositions();
                if (setPositions.size() == 0) {
                    Set<PositionOfPrice> set = new HashSet<>();
                    Calendar c = Calendar.getInstance();
                    set.add(new PositionOfPrice("Здесь будет ваши условия заказа", "Здесь будут ваши условия доставки",
                            new Date(c.YEAR, c.MONTH, c.DAY_OF_MONTH), 000000, account, new Product("Название товара",
                            "Описание товара", null, "Код модели", 000000)));
                    setPositions = set;
                }
                model.addAttribute("listPositions", setPositions);
                model.addAttribute("account", account);
                return "canvas";
            } else {
                model.addAttribute("error", "Обновите страницу по этой ссылке");
                return "canvas";

            }
        }
        return "login";
    }

    @RequestMapping(value = "/changePosition/{positionID}", method = RequestMethod.GET)
    public String changePosition(@PathVariable(value = "positionID") Integer positionID, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
//            String login = auth.getName();
            if (positionID != null) {

                PositionOfPrice positionOfPrice = positionOfPriceService.findPosition(positionID);
//                System.out.println(positionOfPrice.getId() + " -----  " + positionOfPrice.getCost() + " ----- " + positionOfPrice.getProduct().getName());
                model.addAttribute("position", positionOfPrice);
                return "changePage";
            }
            return "login";
        } else return "login";
    }

    //changePositionPost
    @RequestMapping(value = "/changePosition", method = RequestMethod.POST)
    public String changePositionPost(@RequestParam String id, @RequestParam String name,
                                     @RequestParam String codeOfModel, @RequestParam String description,
                                     @RequestParam MultipartFile photo, @RequestParam String amount,
                                     @RequestParam String cost, Model model) {

//        if (!id.isEmpty()) {
//            System.out.println("id пустой");
//        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            PositionOfPrice positionOfPrice = positionOfPriceService.findPosition(Integer.valueOf(id));
            if (positionOfPrice != null) {
                Account account = positionOfPrice.getAccount();
                Product product = positionOfPrice.getProduct();
                if (!name.isEmpty()) {
                    product.setName(name);
                }
                if (!codeOfModel.isEmpty()) {

                    product.setCodeOfModel(codeOfModel);
                    File source = new File(PATH_TO_IMG, product.getPhoto() + IMAGE_EXTENSION);
                    if (source.exists()) {
                        String refPhoto = product.getName() + codeOfModel + account.getLogin();  // IMAGE_EXTENSION
                        File dest = new File(PATH_TO_IMG, refPhoto + IMAGE_EXTENSION);
                        try {
                            FileUtils.copyFile(source, dest);  //
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        source.delete();
                        product.setPhoto(refPhoto);   // сохраняется без расширения потому что без расширения оно приходит на сервер
                    }
                }
                if (!description.isEmpty()) {
                    product.setDescription(description);
                }
                if (!amount.isEmpty() && Integer.parseInt(amount) != product.getAmount()) {
                    product.setAmount(Integer.parseInt(amount));
                }
                if (!cost.isEmpty() && Double.parseDouble(cost) != positionOfPrice.getCost()) {
                    positionOfPrice.setCost(Double.parseDouble(cost));
                }
                if (!photo.isEmpty()) {

                    File file = new File(PATH_TO_IMG, product.getPhoto() + IMAGE_EXTENSION);
                    if (file.exists()) {
                        file.delete();
                    }
//                    System.out.println("фотки нету но в IF вошел ---------------------------");
                    // существует
                    File file1 = new File(PATH_TO_IMG, product.getPhoto() + IMAGE_EXTENSION);
                    try (FileOutputStream fileOut = new FileOutputStream(file1)) {
                        fileOut.write(photo.getBytes());
                        fileOut.flush();
                        fileOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                productService.setProduct(product);
                positionOfPriceService.setPositionOfPrice(positionOfPrice);

                model.addAttribute("position", positionOfPrice);
                return "changePage";

            } else return "login";
        } else return "login";

    }

    @RequestMapping(value = "/givePhoto/{refPhoto}")
    public ResponseEntity<byte[]> takePhoto(@PathVariable(value = "refPhoto") String refPhoto, Model model) {

        byte[] arr;
        System.out.println("handle img " + refPhoto);
        try {
//            String path = "/app-root/data";
//            String path = request.getServletContext().getRealPath("/img");


            File file = new File(PATH_TO_IMG, refPhoto + IMAGE_EXTENSION);

//            if (!file.exists()) {
////                file = new File(PATH_TO_IMG + "defaultPhotoToScreen.png");
//                return new ResponseEntity<byte[]>( "Нет такого файла".getBytes(), HttpStatus.OK);
//            }

            FileInputStream reader = new FileInputStream(file);
            BufferedInputStream inputStream = new BufferedInputStream(reader);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int b;
            while ((b = inputStream.read()) != -1) {
                out.write(b);
            }
//            arr = new byte[inputStream.available()];    // так делать не корректно
//            int s = inputStream.read(arr);
//            if (s == 0)
//                throw new PhotoNotFoundException();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            System.out.println("отдает  фотку на страничку");

            return new ResponseEntity<byte[]>(out.toByteArray(), headers, HttpStatus.OK);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @RequestMapping(value = "/addAccount")
    public String addAccount(Model model) {
        return "registrat";
    }

    @RequestMapping(value = "/addAccount", method = RequestMethod.POST)
    public String addAccount(@RequestParam String login, @RequestParam String pass, @RequestParam String email,
                             @RequestParam String telNumber, @RequestParam String type, Model model) {
        AccountType at = accountTypeService.findByTypeName(type);
        accountService.addAccount(new Account(telNumber, email, pass, login, at));
        model.addAttribute("login", login);
        model.addAttribute("type", type);

        return "login";

    }


    @RequestMapping(value = "/bookingPage", method = RequestMethod.GET)
    public String booking(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String login = auth.getName();
            Account customer = accountService.findAccount(login);
            if (customer != null) {
//            List<Booking> list4 = bookingService.bookingList(customer);
//            List<Booking> list = accountService.findAccount(customer.getLogin()).getBookingSet();
                Set<Booking> set = customer.getBookingSet();
                List<Booking> list = new ArrayList<>(set);

//            if (list.size() != 0) {
//                List<BookingPosition> list1 = (List<BookingPosition>) bookingPositionService.positionsByBooking(list.get(0));
//                List<BookingPosition> listB = list.get(0).getBookingPositions();
//            }
                if (list.size() > 0) {

                    model.addAttribute("bookingList", list);
                } else {
                    model.addAttribute("bookingList", null);
                }
                return "bookingPage";
            } else {
                return "login";
            }
        } else return "login";

    }

    @RequestMapping(value = "/confirmBooking")
    public String confirmBooking(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "bookingPage";
        } else return "login";
    }
//    @RequestMapping(value = "/confirmBooking", method = RequestMethod.POST)
//    public String confirmBooking(@RequestParam String positionID, @RequestParam Integer capacity, Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (!(auth instanceof AnonymousAuthenticationToken)) {
//            BookingPosition bookingPosition = bookingPositionService.findByID(Integer.valueOf(positionID));
//            Account customer = bookingPosition.getBooking().getAccountCustomer();
//            Booking booking = bookingPosition.getBooking();
//            int posCapacity = bookingPosition.getCapacity();
//            Product product = bookingPosition.getProduct();
//
//            if (product != null && product.getId() != 0) {
////                System.out.println("не нал");
//                int amount = product.getAmount();
//                if (amount >= capacity && capacity >= 0) {
//                    amount -= capacity;
//                    product.setAmount(amount);
//                    productService.setProduct(product);
//                    if (posCapacity <= capacity) {
////                bookingPositionService.deleteBookingPosition(bookingPosition);
//                        booking.deleteBookingPosition(bookingPosition);
//                        bookingService.updateBooking(booking);
//
//                        if (booking.getBookingPositions() == null || booking.getBookingPositions().size() == 0) {
//                            customer.deleteBooking(booking);
//                            accountService.updateAccount(customer);
//                        } else {
//                            System.out.println(booking.getBookingPositions().size() + "  лист не обновился");
//                        }
//                    } else {
//                        bookingPosition.setCapacity(posCapacity - capacity);
//                        bookingPositionService.setBookingPosition(bookingPosition);
//                    }
////                if (amount == 0) {
//////                product.setPositions(new HashSet<>());
////                    productService.setProduct(product); //
////                }
//                } else if (capacity >= 0) {
//                    capacity -= amount;
//                    product.setAmount(0);
////            product.setPositions(new HashSet<>());
//                    productService.setProduct(product);
////            productService.deleteProduct(product);
//                    bookingPosition.setCapacity(capacity);
//                    bookingPositionService.setBookingPosition(bookingPosition);
//                }
//            } else {
//                if (capacity >= posCapacity) {
////                System.out.println("тут 1");
//                    booking.deleteBookingPosition(bookingPosition);
//                    bookingService.updateBooking(booking);
//
//                    if (booking.getBookingPositions() == null || booking.getBookingPositions().size() == 0) {
//                        customer.deleteBooking(booking);
//                        accountService.updateAccount(customer);
//                    }
//
//                } else if (capacity >= 0) {
////                System.out.println("тут 2");
//                    bookingPosition.setCapacity(posCapacity - capacity);
//                    bookingPositionService.setBookingPosition(bookingPosition);
//                }
//            }
//
//
////        bookingService.updateBooking(booking); //
////        accountService.updateAccount(customer);
//
//
//            Set<Booking> set = accountService.findAccount(customer.getLogin()).getBookingSet();
//            List<Booking> list = new ArrayList<>(set);
//            if (list != null && list.size() > 0){
//                model.addAttribute("bookingList", list);
//
//            }else {
//                model.addAttribute("bookingList", null);
//            }
//
////            model.addAttribute("login", customer.getLogin());
//
//            return "bookingPage";
//        }else return "login";
//    }

    @RequestMapping(value = "/bookingPosition")
    public String bookingPosition(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            String login = userDetail.getUsername();
            Account account = accountService.findAccount(login);

            if (account.getAccountType().getTypeName().equals("client")) {
                AccountType accountType = accountTypeService.findByTypeName("customer");
                List<Account> accountList = accountService.listAccount(accountType);
                model.addAttribute("accountList", accountList);
                return "home";
            } else return "login";
        } else return "login";

    }

    @RequestMapping(value = "/bookingPosition", method = RequestMethod.POST)
    public String bookingPosition(@RequestParam String positionID, @RequestParam Integer capacity, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String clientLogin = auth.getName();
            Account client = accountService.findAccount(clientLogin);
            PositionOfPrice position = positionOfPriceService.findPosition(Integer.valueOf(positionID));
            String s = position.getAccount().getLogin();
            Account customer = accountService.findAccount(s);
            Product product = position.getProduct();

            Booking booking = bookingService.findForClient(client, customer);
            if (booking == null) {
                booking = new Booking(client, customer); //, customer
                bookingService.save(booking);
                System.out.println(booking.getId() + " это ID заказа ");
                BookingPosition bookingPosition = new BookingPosition(capacity, booking, product); //, booking
                bookingPositionService.setBookingPosition(bookingPosition);
//            booking.addBookingPosition(bookingPosition);
//            customer.addBookingList(booking);
                accountService.updateAccount(customer);
            } else {
                System.out.println(booking.getId() + " это ID заказа else");
                BookingPosition bookingPosition = new BookingPosition(capacity, booking, product); // booking ,
                bookingPositionService.setBookingPosition(bookingPosition);
//            booking.addBookingPosition(bookingPosition);
                bookingService.updateBooking(booking);
            }

            //не надо сохранять т к оно сохраняется при создании позции

//        if (booking == null){
//            booking = new Booking(client, customer);
//            bookingService.save(booking);    ///
//            System.out.println(booking.getId() + "   его ID");
//            BookingPosition bookingPosition = new BookingPosition(capacity, booking, product);
//            bookingPositionService.setBookingPosition(bookingPosition);
//
//            customer.addBookingList(booking);
//
//            accountService.updateAccount(customer);
//        }else {
//            System.out.println(booking.getId()+"   его ID");
////            bookingService.updateBooking(booking);
//            BookingPosition bookingPosition = new BookingPosition(capacity, booking, product);
//            booking.addBookingPosition(bookingPosition);
//            bookingPositionService.setBookingPosition(bookingPosition);
////            bookingService.updateBooking(booking);
//        }


            List<PositionOfPrice> listPositions = accountService.listPositions(customer);
            model.addAttribute("listPositions", listPositions);
            model.addAttribute("login", customer.getLogin());
            model.addAttribute("account", customer);

            return "canvas";
        } else return "login";

    }


    @RequestMapping(value = "/deleteAllBooking", method = RequestMethod.GET)
    public String deleteAllBooking(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName();
        Account account = accountService.findAccount(login);
        account.deleteAllBooking();
//        bookingService.deleteAllBooking(account);
        accountService.updateAccount(account);
//        model.addAttribute("login", login);
        model.addAttribute("bookingList", null);
        return "bookingPage";
    }

    @RequestMapping(value = "/changeBackground")
    public String changeBackground(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            String login = userDetail.getUsername();
            Account account = accountService.findAccount(login);

            if (account.getAccountType().getTypeName().equals("customer")) {
                Set<PositionOfPrice> set = (Set<PositionOfPrice>) account.getPricePositions();
                List<PositionOfPrice> list = new ArrayList<>(set);
                model.addAttribute("listPositions", list);
                model.addAttribute("account", account);
                return "canvas";
            } else return "login";
        } else return "login";
    }

    @RequestMapping(value = "/changeBackground", method = RequestMethod.POST)
    public String changeBackground(@RequestParam MultipartFile photoBackground, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            String login = userDetail.getUsername();
            Account account = accountService.findAccount(login);
            if (account.getAccountType().getTypeName().equals("customer")) {
                StringBuilder refPhoto = new StringBuilder();
                String ref = photoBackground.getOriginalFilename();
                refPhoto.append(account.getLogin()).append(ref.substring(0, ref.indexOf(".")));
                if (account.getPhotoBackground1() == null) {
//                    refPhoto.append(photoBackground.getOriginalFilename());
                    account.setPhotoBackground1(refPhoto.toString());
                } else if (account.getPhotoBackground2() == null) {
//                    refPhoto.append();
                    account.setPhotoBackground2(refPhoto.toString());
                } else if (account.getPhotoBackground3() == null) {
//                    refPhoto.append("photoBackground3");
                    account.setPhotoBackground3(refPhoto.toString());
                }

                File file = new File(PATH_TO_IMG, refPhoto.toString() + IMAGE_EXTENSION);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(photoBackground.getBytes());
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    model.addAttribute("error", e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                accountService.updateAccount(account);

                Set<PositionOfPrice> set = (Set<PositionOfPrice>) account.getPricePositions();
                List<PositionOfPrice> list = new ArrayList<>(set);
                model.addAttribute("listPositions", list);
                model.addAttribute("account", account);
                return "canvas";
            } else return "login";
        } else return "login";
    }

    @RequestMapping(value = "/deleteBackground")
    public String deleteBackground(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            String login = userDetail.getUsername();
            Account account = accountService.findAccount(login);

            if (account.getAccountType().getTypeName().equals("customer")) {
                Set<PositionOfPrice> set = (Set<PositionOfPrice>) account.getPricePositions();
                List<PositionOfPrice> list = new ArrayList<>(set);
                model.addAttribute("listPositions", list);
                model.addAttribute("account", account);
                return "canvas";
            } else return "login";
        } else return "login";
    }

    @RequestMapping(value = "/deleteBackground", method = RequestMethod.POST)
    public String deleteBackground(@RequestParam(name = "photoBackground1", required = false) String photoBackground1,
                                   @RequestParam(name = "photoBackground2", required = false) String photoBackground2,
                                   @RequestParam(name = "photoBackground3", required = false) String photoBackground3,
                                   Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            String login = userDetail.getUsername();
            Account account = accountService.findAccount(login);

            System.out.println(photoBackground1);
            System.out.println(photoBackground2);
            System.out.println(photoBackground3);
            if (account.getAccountType().getTypeName().equals("customer")) {

                File file1 = new File(PATH_TO_IMG, account.getPhotoBackground1() + IMAGE_EXTENSION);
                File file2 = new File(PATH_TO_IMG, account.getPhotoBackground2() + IMAGE_EXTENSION);
                File file3 = new File(PATH_TO_IMG, account.getPhotoBackground3() + IMAGE_EXTENSION);
                String refphoto;

                if (photoBackground1 == null && photoBackground2 == null && photoBackground3 == null) {
                    Set<PositionOfPrice> set = (Set<PositionOfPrice>) account.getPricePositions();
                    List<PositionOfPrice> list = new ArrayList<>(set);
                    model.addAttribute("listPositions", list);
                    model.addAttribute("account", account);
                    return "canvas";
                } else if (photoBackground1 != null && photoBackground2 != null && photoBackground3 != null) {
                    if (file1.exists()) {
                        file1.delete();
                    }
                    if (file2.exists()) {
                        file2.delete();
                    }
                    if (file3.exists()) {
                        file3.delete();
                    }
                    account.setPhotoBackground1(null);
                    account.setPhotoBackground2(null);
                    account.setPhotoBackground3(null);
                } else if (photoBackground1 != null && photoBackground2 != null) {
                    if (file1.exists()) {
                        file1.delete();
                    }
                    account.setPhotoBackground1(null);
                    if (file2.exists()) {
                        file2.delete();
                    }
                    account.setPhotoBackground2(null);
                    refphoto = account.getPhotoBackground3();
                    if (refphoto != null) {
                        account.setPhotoBackground1(refphoto);
                        account.setPhotoBackground3(null);
                    }
                } else if (photoBackground1 != null && photoBackground3 != null) {
                    if (file1.exists()) {
                        file1.delete();
                    }
                    account.setPhotoBackground1(null);
                    if (file3.exists()) {
                        file3.delete();
                    }
                    account.setPhotoBackground3(null);
                    refphoto = account.getPhotoBackground2();
                    if (refphoto != null) {
                        account.setPhotoBackground1(refphoto);
                        account.setPhotoBackground2(null);
                    }
                } else if (photoBackground2 != null && photoBackground3 != null) {
                    if (file2.exists()) {
                        file2.delete();
                    }
                    if (file3.exists()) {
                        file3.delete();
                    }
                    account.setPhotoBackground2(null);
                    account.setPhotoBackground3(null);
                } else {
                    if (photoBackground1 != null) {
                        if (file1.exists()) {
                            file1.delete();
                        }
                        account.setPhotoBackground1(null);
                        String refPhoto2 = account.getPhotoBackground2();
                        String refPhoto3 = account.getPhotoBackground3();
                        if (refPhoto2 != null) {
                            account.setPhotoBackground1(refPhoto2);
                            account.setPhotoBackground2(null);
                        }
                        if (refPhoto3 != null) {
                            account.setPhotoBackground2(refPhoto3);
                            account.setPhotoBackground3(null);
                        }
                    } else if (photoBackground2 != null) {
                        if (file2.exists()) {
                            file2.delete();
                        }
                        account.setPhotoBackground2(null);
                        String refPhoto3 = account.getPhotoBackground3();
                        if (refPhoto3 != null) {
                            account.setPhotoBackground2(refPhoto3);
                            account.setPhotoBackground3(null);
                        }
                    } else if (photoBackground3 != null) {
                        if (file3.exists()) {
                            file3.delete();
                        }
                        account.setPhotoBackground3(null);
                    }
                }

                accountService.updateAccount(account);

                Set<PositionOfPrice> set = (Set<PositionOfPrice>) account.getPricePositions();
                List<PositionOfPrice> list = new ArrayList<>(set);
                model.addAttribute("listPositions", list);
                model.addAttribute("account", account);
                return "canvas";
            } else return "login";
        } else return "login";
    }


}

























