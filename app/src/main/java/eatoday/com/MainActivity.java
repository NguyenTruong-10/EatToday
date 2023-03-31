package eatoday.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eatoday.com.authentication.LoginFragment;
import eatoday.com.authentication.SignUpFragment;
import eatoday.com.databinding.ActivityMainBinding;
import eatoday.com.databinding.FragmentProfilesBinding;
import eatoday.com.ui.AccountFragment;
import eatoday.com.ui.AccountRecoveryFragment;
import eatoday.com.ui.ChangePasswordFragment;
import eatoday.com.ui.Detail_Food_Fragment;
import eatoday.com.ui.EditPostFragment;
import eatoday.com.ui.EmailVerificationFragment;
import eatoday.com.ui.HomeFragment;
import eatoday.com.ui.MyListFragment;
import eatoday.com.ui.MyPostFragment;
import eatoday.com.ui.NotificationFragment;
import eatoday.com.ui.ProfileFragment;
import eatoday.com.ui.ReAuthenticateUserFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private HomeFragment homeFragment = new HomeFragment();
    private NotificationFragment notificationFragment = new NotificationFragment();
    //    private Detail_Food_Fragment detail_food_fragment = new Detail_Food_Fragment();
    private MyListFragment myListFragment = new MyListFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private MyPostFragment myPostFragment = new MyPostFragment();
    private AccountFragment accountFragment = new AccountFragment();
    private LoginFragment loginFragment = new LoginFragment();
    private SignUpFragment signUpFragment = new SignUpFragment();
    private ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
    private ReAuthenticateUserFragment reAuthenticateUserFragment = new ReAuthenticateUserFragment();
    private AccountRecoveryFragment accountRecoveryFragment = new AccountRecoveryFragment();
    private EmailVerificationFragment verificationFragment = new EmailVerificationFragment();
    private EditPostFragment editPostFragment = new EditPostFragment();
    private FragmentManager fragmentManager;
    private Fragment active = homeFragment;

    private FirebaseAuth mAuth;

    private static final String RELOAD = "Reload mainActivity info";

    private static final String ANONYMOUS = "Anonymous sign in";

    @Override
    protected void onResume() {
        super.onResume();
        //getting Root View that gets focus
        View rootView = ((ViewGroup) findViewById(android.R.id.content)).
                getChildAt(0);
        rootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(MainActivity.this);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(homeFragment);

        binding.bottomNavigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();
        profileFragment.setCallback(new ProfileFragment.Callback() {
            @Override
            public void onClickFood() {
                openMyPostFragment();
            }

            @Override
            public void onClickUser() {
                openAccountFragment();
            }

            @Override
            public void onLogOut() { logOut(); }

            @Override
            public void onClickList() {
                openMylistFragment();
            }

            @Override
            public void onClickChangePassword() {
                replaceFragment(reAuthenticateUserFragment);
            }

            @Override
            public void onClickEmailVerify() {
                replaceFragment(verificationFragment);
            }
        });
        myPostFragment.setCallback(() -> replaceFragment(profileFragment));
        loginFragment.setCallback(new LoginFragment.Callback() {
            @Override
            public void onSignIn() {
                afterSignIn();
            }

            @Override
            public void notRegisterSignUp() {
                replaceFragment(signUpFragment);
            }
        });
        myListFragment.setCallback(() -> replaceFragment(profileFragment));

        signUpFragment.setCallback(new SignUpFragment.Callback() {
            @Override
            public void onSignUp() {
                afterSignIn();
            }

            @Override
            public void alreadyRegistered() {
                replaceFragment(loginFragment);
            }
        });

        accountFragment.setCallback(new AccountFragment.Callback() {
            @Override
            public void onConfirmUpdate() {
                replaceFragment(profileFragment);
            }

            @Override
            public void onBack() {
                replaceFragment(profileFragment);
            }
        });

        changePasswordFragment.setCallback(new ChangePasswordFragment.Callback() {
            @Override
            public void onConfirmChangePass() { replaceFragment(profileFragment); }


        });
        reAuthenticateUserFragment.setCallback(new ReAuthenticateUserFragment.Callback() {
            @Override
            public void onBack() {
                replaceFragment(profileFragment);
            }

            @Override
            public void onContinue() {
                replaceFragment(changePasswordFragment);
            }

            @Override
            public void onForgotPasswordClicked() {
                replaceFragment(accountRecoveryFragment);
            }
        });
        verificationFragment.setCallback(() -> replaceFragment(profileFragment));
        editPostFragment.setCallback(new EditPostFragment.Callback(){

            @Override
            public void onBack() {
                replaceFragment(myListFragment);
            }
        });
    }

    private BottomNavigationView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.home:
                replaceFragment(homeFragment);
                break;
            case R.id.notification:
                replaceFragment(notificationFragment);
                break;
            case R.id.profile:
                updateUI(mAuth.getCurrentUser());
                break;
        }
        return true;
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //call super
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void hideKeyboard(Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
    }

    public void openMyPostFragment() {
        // use replace
        FragmentTransaction fragmentTransaction5 = fragmentManager.beginTransaction();
        fragmentTransaction5.replace(R.id.frameLayout, myPostFragment).addToBackStack(null).commit();
    }

    public void openAccountFragment() {
        // use replace
        FragmentTransaction fragmentTransaction5 = fragmentManager.beginTransaction();
        fragmentTransaction5.replace(R.id.frameLayout, accountFragment).addToBackStack(null).commit();
    }

    public void openMylistFragment() {
        // use replace
        FragmentTransaction fragmentTransaction5 = fragmentManager.beginTransaction();
        fragmentTransaction5.replace(R.id.frameLayout, myListFragment).addToBackStack(null).commit();
    }


    public void logOut() {
        updateUI(null);
    }

    public void afterSignIn() {
        updateUI(mAuth.getCurrentUser());

    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out  // popExit
        );
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    }

    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e(RELOAD, "Success");
                    updateUI(mAuth.getCurrentUser());
                } else {
                    Log.e(RELOAD, "Error 404", task.getException());
                    Toast.makeText(getApplicationContext(),
                            "Failed to reload user",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        }); //reload user information - Testing only
    }

    private void updateUI(FirebaseUser user) {
        //user log out
        if (user != null) {
            replaceFragment(profileFragment);
        } else {
            replaceFragment(loginFragment);
        }
    }
}


