package ma.baxtiyorjon.LinkShortener.controllers;

import ma.baxtiyorjon.LinkShortener.models.UsersEntity;
import ma.baxtiyorjon.LinkShortener.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UsersEntity>> getAll(){
        return usersService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<UsersEntity> getMe(HttpServletRequest request){
        return usersService.getUser(request);
    }

    @PutMapping("/changePlan")
    public ResponseEntity<Map<String, Object>> changePlan(@RequestParam("toPro") Boolean toPro, HttpServletRequest request){
        return usersService.changePlan(toPro, request);
    }

    @PostMapping("/changePassword")
    public Boolean changePassword(@RequestParam("password") String password, HttpServletRequest request){
        return usersService.changePassword(password, request);
    }
}
