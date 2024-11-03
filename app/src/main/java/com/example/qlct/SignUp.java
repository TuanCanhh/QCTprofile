package com.example.qlct;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qlct.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity {

    // Khai báo biến
    EditText etGmail, etPass, etName, etAge, etPhone;
    Button btnSignup;
    TextView loginNow;
    FirebaseAuth mAuth;
    View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Xử lý chế độ hiển thị toàn màn hình
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ Id
        etGmail = findViewById(R.id.gmail);
        etPass = findViewById(R.id.pass);
        etName = findViewById(R.id.name);
        etAge = findViewById(R.id.age);
        etPhone = findViewById(R.id.phone);
        btnSignup = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progressBar);
        loginNow = findViewById(R.id.loginnow);

        // Xử lý sự kiện khi nhấn vào "login now"
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        // Xử lý sự kiện khi nhấn nút đăng ký
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                // Lấy dữ liệu từ các ô nhập liệu
                String email = etGmail.getText().toString().trim();
                String password = etPass.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String ageString = etAge.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                // Kiểm tra các trường nhập liệu
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SignUp.this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(ageString)) {
                    Toast.makeText(SignUp.this, "Vui lòng nhập tuổi", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                int age;
                try {
                    age = Integer.parseInt(ageString);
                } catch (NumberFormatException e) {
                    Toast.makeText(SignUp.this, "Tuổi không hợp lệ", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(SignUp.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (phone.length() != 10) {
                    Toast.makeText(SignUp.this, "Số điện thoại phải có 10 chữ số", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUp.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUp.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Tạo tài khoản người dùng với Firebase
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            User newUser = new User(name, phone, age, email, password); // Cập nhật thông tin người dùng

                            // Thêm người dùng vào Firestore trong collection 'users'
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(userId)
                                    .set(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUp.this, "User added to Firestore successfully", Toast.LENGTH_SHORT).show();
                                                // Chuyển sang trang login hoặc màn hình chính
                                                startActivity(new Intent(SignUp.this, Login.class));
                                            } else {
                                                Toast.makeText(SignUp.this, "Failed to add user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.i("check", task.getException().getMessage());
                                            }
                                        }
                                    });

                            startActivity(new Intent(SignUp.this, Login.class));
                        } else {
                            Toast.makeText(SignUp.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
