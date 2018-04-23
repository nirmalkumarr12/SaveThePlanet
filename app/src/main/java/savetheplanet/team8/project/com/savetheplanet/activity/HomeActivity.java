package savetheplanet.team8.project.com.savetheplanet.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.fragment.AllProductListFragment;
import savetheplanet.team8.project.com.savetheplanet.preferences.Preferences;

public class HomeActivity extends BaseActivity {
    protected Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private Drawer result = null;
    AccountHeader headerResult;
    ProfileDrawerItem userProfile;
    String imageUrl, firstName, lastName, email = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_home);
        toolbar = findViewById(R.id.toolbar);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageUrl = extras.getString(Preferences.IMAGE_URL);
            firstName = extras.getString(Preferences.FIRST_NAME);
            lastName = extras.getString(Preferences.LAST_NAME);

        } else {
            imageUrl = sharedPreferences.getString(Preferences.IMAGE_URL, "");
            firstName = sharedPreferences.getString(Preferences.FIRST_NAME, "");
            lastName = sharedPreferences.getString(Preferences.LAST_NAME, "");
            email = sharedPreferences.getString(Preferences.EMAIL, "");
        }

        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        } else {
            onAuthFailure();
        }


        Fragment home_fragment = new AllProductListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, home_fragment);
        transaction.commit();

        final PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Browse Products").withIdentifier(1);
        final PrimaryDrawerItem profile = new PrimaryDrawerItem().withName("My Products").withIdentifier(2);
        final PrimaryDrawerItem logout = new PrimaryDrawerItem().withName("Logout").withIdentifier(4);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).fit().centerCrop().into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

        userProfile = new ProfileDrawerItem().withName(firstName + " " + lastName).withEmail(email).withIcon(imageUrl);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(userProfile)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(true)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(home)
                .addDrawerItems(profile)
                .addDrawerItems(logout)
                .buildForFragment();


        result.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                int drawItemId = (int) drawerItem.getIdentifier();
                Intent intent;
                Fragment fragment = null;
                switch (drawItemId) {
                    case 1:
                        fragment = new AllProductListFragment();
                        break;
                    case 2:
                        fragment = new AllProductListFragment();
                        break;
                    case 4:
                        FirebaseAuth.getInstance().signOut();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        intent = new Intent(HomeActivity.this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        break;

                    default:
                        fragment = new AllProductListFragment();
                        break;
                }

                if (fragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.commit();
                }

                return false;
            }
        });

    }


    private void onAuthFailure() {
        Intent intent = new Intent(this, SignInSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
}
