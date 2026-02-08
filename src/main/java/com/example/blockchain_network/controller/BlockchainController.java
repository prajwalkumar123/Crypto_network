package com.example.blockchain_network.controller;



import com.example.blockchain_network.entity.User;
import com.example.blockchain_network.entity.Transaction;
import com.example.blockchain_network.service.UserService;
import com.example.blockchain_network.service.TokenService;
import com.example.blockchain_network.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class BlockchainController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TransactionService transactionService;

    // Home page
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Login page
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    // Login processing
    @PostMapping("/login")
    public String login(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<User> loggedUser = userService.loginUser(user.getUsername(), user.getPassword());
        if (loggedUser.isPresent()) {
            session.setAttribute("user", loggedUser.get());
            return "redirect:/node";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    // Register page
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Register processing
    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    // Node dashboard
    @GetMapping("/node")
    public String nodeDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("userTokens", tokenService.findTokensByUserAndNode(user.getId(), user.getNodeNumber()));
        model.addAttribute("nodeTransactions", transactionService.findTransactionsForNode(user.getNodeNumber()));
        model.addAttribute("nextNode", userService.getNextNode(user.getNodeNumber()));
        model.addAttribute("prevNode", userService.getPreviousNode(user.getNodeNumber()));
        
        return "node";
    }

    // Token page
    @GetMapping("/token")
    public String tokenPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("userTokens", tokenService.findTokensByUser(user.getId()));
        return "token";
    }

    // Create token
    @PostMapping("/token/create")
    public String createToken(@RequestParam String tokenText, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            tokenService.createToken(user.getId(), user.getNodeNumber(), tokenText);
            redirectAttributes.addFlashAttribute("success", "Token created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating token: " + e.getMessage());
        }
        
        return "redirect:/token";
    }

    // Transfer page
    @GetMapping("/transfer")
    public String transferPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("availableTokens", tokenService.findTokensByUserAndNode(user.getId(), user.getNodeNumber()));
        model.addAttribute("nextNode", userService.getNextNode(user.getNodeNumber()));
        model.addAttribute("prevNode", userService.getPreviousNode(user.getNodeNumber()));
        
        return "transfer";
    }

    // Process transfer
    @PostMapping("/transfer/process")
    public String processTransfer(@RequestParam Long tokenId, @RequestParam String encryptionKey, @RequestParam String direction, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            int destinationNode;
            if ("next".equals(direction)) {
                destinationNode = userService.getNextNode(user.getNodeNumber());
            } else {
                destinationNode = userService.getPreviousNode(user.getNodeNumber());
            }
            
            Transaction transaction = transactionService.processTransfer(
                user.getNodeNumber(), destinationNode, tokenId, encryptionKey);
            
            redirectAttributes.addFlashAttribute("success", 
                "Token transferred successfully! Transaction ID: " + transaction.getTransactionId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Transfer failed: " + e.getMessage());
        }
        
        return "redirect:/transfer";
    }

    // Received page
    @GetMapping("/received")
    public String receivedPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("receivedTokens", tokenService.findTokensForDecryption(user.getNodeNumber()));
        model.addAttribute("receivedTransactions", transactionService.findTransactionsByDestinationNode(user.getNodeNumber()));
        
        return "received";
    }

    // Decrypt token
    @PostMapping("/received/decrypt")
    public String decryptToken(@RequestParam Long tokenId,  @RequestParam String decryptionKey, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            String decryptedText = tokenService.decryptToken(tokenId, decryptionKey);
            redirectAttributes.addFlashAttribute("success", "Token decrypted successfully!");
            redirectAttributes.addFlashAttribute("decryptedText", decryptedText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Decryption failed: " + e.getMessage());
        }
        
        return "redirect:/received";
    }

    // Ledger page
    @GetMapping("/ledger")
    public String ledgerPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("allTransactions", transactionService.findAllTransactions());
        model.addAttribute("nodeTransactions", transactionService.findTransactionsForNode(user.getNodeNumber()));
        model.addAttribute("totalTransactions", transactionService.getTotalTransactions());
        model.addAttribute("completedTransactions", transactionService.getTransactionCountByStatus("COMPLETED"));
        model.addAttribute("pendingTransactions", transactionService.getTransactionCountByStatus("PENDING"));
        
        return "ledger";
    }

    // Profile page
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("userTokenCount", tokenService.findTokensByUser(user.getId()).size());
        model.addAttribute("userTransactionCount", transactionService.findTransactionsForNode(user.getNodeNumber()).size());
        
        return "profile";
    }

    // Network topology page
    @GetMapping("/network")
    public String networkPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("totalNodes", 10);
        model.addAttribute("activeTransactions", transactionService.getPendingTransactions().size());
        model.addAttribute("completedTransactions", transactionService.getCompletedTransactions().size());
        
        return "network";
    }

    // Dashboard page
    @GetMapping("/dashboard")
    public String dashboardPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("totalTokens", tokenService.getTotalTokens());
        model.addAttribute("totalTransactions", transactionService.getTotalTransactions());
        model.addAttribute("recentTransactions", transactionService.findAllTransactions().stream().limit(10).toList());
        
        return "dashboard";
    }

    // Update profile
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String email, HttpSession session,RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            user.setEmail(email);
            User updatedUser = userService.updateUser(user);
            session.setAttribute("user", updatedUser);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }

    // Change password
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            if (!newPassword.equals(confirmPassword)) {
                throw new Exception("New passwords do not match");
            }
            
            userService.changePassword(user.getId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error changing password: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // API endpoints for AJAX calls
    @GetMapping("/api/node/{nodeNumber}/status")
    @ResponseBody
    public String getNodeStatus(@PathVariable int nodeNumber) {
        long transactionCount = transactionService.getTransactionCountByNode(nodeNumber);
        return "{\"nodeNumber\":" + nodeNumber + ",\"transactionCount\":" + transactionCount + ",\"status\":\"active\"}";
    }

    @GetMapping("/api/transactions/recent")
    @ResponseBody
    public List<Transaction> getRecentTransactions() {
        return transactionService.findAllTransactions().stream().limit(5).toList();
    }
}
