package com.example.qlct;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText name, phone, age, gmail, password;
    private Button logout, editprofile;
    private boolean isEditing = false; // Biến để theo dõi trạng thái chỉnh sửa
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo FirebaseAuth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Lấy userId của người dùng hiện tại
        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ Id
        logout = view.findViewById(R.id.logout);
        editprofile = view.findViewById(R.id.editprofile);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        age = view.findViewById(R.id.age1);
        gmail = view.findViewById(R.id.gmail);
        password = view.findViewById(R.id.password); // Ánh xạ ô Password

        // Gọi phương thức để lấy và hiển thị thông tin người dùng
        loadUserProfile();

        // Thiết lập lắng nghe sự kiện cho nút logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đăng xuất người dùng
                mAuth.signOut();
                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();

                // Chuyển đến màn hình đăng nhập
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Thiết lập lắng nghe sự kiện cho nút editButton
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing) {
                    // Nếu đang trong chế độ chỉnh sửa, thì lưu dữ liệu
                    saveUserData();
                } else {
                    // Nếu không trong chế độ chỉnh sửa, bật chế độ chỉnh sửa
                    enableEditing(true);
                }
            }
        });

        return view;
    }

    private void enableEditing(boolean enable) {
        // Bật hoặc tắt chế độ chỉnh sửa cho các EditText
        name.setEnabled(enable);
        phone.setEnabled(enable);
        age.setEnabled(enable);
        gmail.setEnabled(enable);
        password.setEnabled(enable); // Bật chỉnh sửa cho mật khẩu

        if (enable) {
            // Hiển thị mật khẩu dưới dạng văn bản khi ở chế độ chỉnh sửa
            password.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            editprofile.setText("SAVE");  // Đổi nút thành "SAVE"
            Toast.makeText(getActivity(), "Edit Mode Enabled", Toast.LENGTH_SHORT).show();
        } else {
            // Ẩn mật khẩu sau khi lưu, chuyển lại về dạng mật khẩu
            password.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editprofile.setText("EDIT PROFILE");  // Đổi nút lại thành "EDIT PROFILE"
            Toast.makeText(getActivity(), "Profile Saved", Toast.LENGTH_SHORT).show();
        }

        isEditing = enable;  // Cập nhật trạng thái chỉnh sửa
    }

    private void saveUserData() {
        // Lấy dữ liệu từ EditText
        String userName = name.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String ageString = age.getText().toString().trim();
        String userGmail = gmail.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userName.isEmpty() || userPhone.isEmpty() || ageString.isEmpty() || userGmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi tuổi thành số nguyên
        Long userAge = Long.parseLong(ageString);

        // Tạo một bản đồ dữ liệu để lưu vào Firestore
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", userName);
        userUpdates.put("phone", userPhone);
        userUpdates.put("age", userAge);
        userUpdates.put("gmail", userGmail);
        userUpdates.put("password", userPassword); // Thêm mật khẩu vào Firestore

        // Cập nhật dữ liệu người dùng trong Firestore
        db.collection("users").document(userId)
                .update(userUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            enableEditing(false);  // Tắt chế độ chỉnh sửa sau khi lưu
                        } else {
                            Toast.makeText(getActivity(), "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadUserProfile() {
        if (userId == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truy xuất tài liệu người dùng từ Firestore
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Lấy dữ liệu từ document
                                String userName = document.getString("name");
                                String userPhone = document.getString("phone");
                                Long Age = document.getLong("age");
                                String ageString = String.valueOf(Age);
                                String userGmail = document.getString("gmail");
                                String userPassword = document.getString("password"); // Lấy mật khẩu

                                // Gán dữ liệu vào các EditText trên màn hình Profile
                                name.setText(userName != null ? userName : "No name");
                                phone.setText(userPhone != null ? userPhone : "No phone");
                                age.setText(ageString);
                                gmail.setText(userGmail != null ? userGmail : "No email");
                                password.setText(userPassword != null ? userPassword : ""); // Hiển thị mật khẩu
                            } else {
                                Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed to load user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
