package at.fh.swenga.service;

import at.fh.swenga.model.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by felix on 13/06/2017.
 */
public class PasswordMatchesValidator
        implements ConstraintValidator<User.PasswordMatches, Object> {

    @Override
    public void initialize(User.PasswordMatches constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        User user = (User) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}