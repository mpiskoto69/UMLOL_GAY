package managers;

import bank.storage.StorableList;
import users.Company;
import users.Customer;
import users.Individual;
import users.User;

public class UserManager {
    private static final UserManager instance = new UserManager();
    private StorableList<User> users = new StorableList<>();

    private UserManager() {
    }

    public static UserManager getInstance() {
        return instance;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User findUserByUsername(String username) {
        for (User u : users) {
            String un = u.getUsername();
            if (un != null && un.trim().equals(username.trim())) {
                return u;
            }
        }
        return null;
    }

    public StorableList<User> getAllUsers() {
        return users;
    }

    public Customer findCustomerByVat(String vat) {
        for (User user : users) {
            if (user instanceof Customer && ((Customer) user).getVatNumber().equals(vat)) {
                return (Customer) user;
            }
        }
        return null;
    }

    public void createIndividual(String vat, String legalName, String username, String password) {
        Individual ind = new Individual(vat, legalName, username, password);
        users.add(ind);
    }

    public void createCompany(String username, String password, String vatNumber, String legalName) {
        Company comp = new Company(username, password, vatNumber, legalName);
        users.add(comp);
    }

    public void addUsers(StorableList<User> users) {
        this.users.addAll(users);
    }

    public void resetPassword(String username, String newPassword) {
        User u = findUserByUsername(username);
        if (u == null)
            throw new IllegalArgumentException("User not found");

        u.setPassword(newPassword);
    }

    public void clearAll() {
        users.clear();
    }

}
