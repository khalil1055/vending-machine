package com.example.spring.services;

import com.example.spring.entities.Privilege;
import com.example.spring.entities.Product;
import com.example.spring.entities.Role;
import com.example.spring.entities.User;
import com.example.spring.enums.Privileges;
import com.example.spring.exceptions.ProductErrorException;
import com.example.spring.exceptions.RoleErrorException;
import com.example.spring.exceptions.UserErrorException;
import com.example.spring.repositories.ProductRepository;
import com.example.spring.repositories.RoleRepository;
import com.example.spring.repositories.UserRepository;
import com.example.spring.requests.CreateUserRequest;
import com.example.spring.responses.RoleResponse;
import com.example.spring.util.BCryptUtil;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User createUser(CreateUserRequest createUserRequest) {
        if(createUserRequest.getUsername() == null || createUserRequest.getPassword() == null){
            throw new UserErrorException("username and password are required");
        }

        Optional<User> optionalUser = userRepository
                .findByUsername(createUserRequest.getUsername());

        //check if requested username already exists or not
        if (optionalUser.isPresent()) {
            throw new UserErrorException("username taken");
        }

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        //hash the password before saving
        user.setPassword(BCryptUtil.hash(createUserRequest.getPassword()));

        //set the user balance if its provided (default is 0)
        if(createUserRequest.getDeposit() != null){
            user.setDeposit(createUserRequest.getDeposit());
        }

        userRepository.save(user);

        return user;
    }

    /**
     * Get authenticated user roles
     *
     * @param user the authenticated user
     * @return List<RoleResponse>
     */
    public List<RoleResponse> getUserRoles(User user) {
        List<RoleResponse> roleResponses = new ArrayList<>();

        for (Role role : user.getRoles()) {
            roleResponses.add(new RoleResponse(role.getId(), role.getName()));
        }

        return roleResponses;
    }

    /**
     * add role to the authenticated user
     * if the role is already applied it returns
     *
     * @param user the authenticated user
     * @param roleId the role to apply
     */
    public void addUserRole(User user, Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleErrorException("role " + roleId + " not found"));

        Collection<Role> userRoles = user.getRoles();

        if(!userRoles.isEmpty()){
            for(Role r : userRoles){
                if (Objects.equals(r.getName(), role.getName())){
                    return;
                }
            }
        }

        userRoles.add(role);

        userRepository.save(user);
    }

    /**
     * Deposit to the authenticated user
     *
     * @param user the authenticated user
     * @param amount amount to add
     * @return User
     */
    public User depositToUser(User user, Integer amount) {
        Integer deposit = user.getDeposit();
        user.setDeposit(deposit + amount);

        userRepository.save(user);

        return user;
    }

    /**
     * Buy product by the authenticated user
     *
     * @param user the user
     * @param productId the product to buy
     * @param quantity the quantity to buy
     * @return String
     */
    @Transactional
    public String buyProduct(User user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductErrorException("product not found"));

        int totalProductsPrice = quantity * product.getCost();
        User u = this.updateDepositBalance(user, totalProductsPrice);

        productService.updateProductStock(product, quantity);

        JSONObject res = new JSONObject();
        res.put("totalSpent", totalProductsPrice);
        res.put("remainingBalance", u.getDeposit());
        res.put("change", getUserChange(user.getDeposit()));

        return res.toString();
    }

    /**
     * Checks if the authenticated user has a privilege
     *
     * @param user the authenticated user
     * @param privilege the privilege to check for
     */
    public void userHasPrivilege(User user, Privileges privilege) {
        List<Privilege> userPrivileges = new ArrayList<>();

        System.out.println(user.getRoles().toString() + " :: here");

        if (user.getRoles().isEmpty()){
            throw new RoleErrorException("user " + user.getUsername() + " has no roles");
        }

        for (Role r : user.getRoles()) {
            userPrivileges.addAll(r.getPrivileges());
        }

        boolean can = userPrivileges.stream().anyMatch(p -> Objects.equals(p.getName(), privilege.toString()));

        if (!can) {
            throw new UserErrorException("user " + user.getUsername() + " does not have privilege " + privilege.toString());
        }
    }

    /**
     * Update user balance
     *
     * @param user the authenticated user
     * @param amount the amount to add
     * @return User
     */
    private User updateDepositBalance(User user, Integer amount) {
        int userDeposit = user.getDeposit();

        if (userDeposit < amount) {
            throw new UserErrorException("balance insufficient");
        }

        user.setDeposit(userDeposit - amount);
        userRepository.save(user);

        return user;
    }

    /**
     * Reset user balance to 0
     *
     * @param user the authenticated user
     * @return User
     */
    public User resetUserDeposit(User user) {
        user.setDeposit(0);

        userRepository.save(user);

        return user;
    }

    /**
     * Get remaining user change in coins {100, 50, 20, 10, 5}
     *
     * @param deposit the authenticated user
     * @return HashMap<Integer, Integer>
     */
    private HashMap<Integer, Integer> getUserChange(Integer deposit) {
        HashMap<Integer, Integer> change = new HashMap<>();

        int balance = deposit;

        int[] coins = {100, 50, 20, 10, 5};

        for (int coin : coins) {
            change.put(coin, balance / coin);
            balance = balance % coin;
        }

        return change;
    }
}