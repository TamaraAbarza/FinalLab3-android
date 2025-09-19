package com.ulp.eventapp.ui.organizador.usuario;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ulp.eventapp.R;
import com.ulp.eventapp.model.User;
import com.ulp.eventapp.request.ApiClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolderVH>  {
    private List<User> userList;
    private Context context;
    private LayoutInflater li;

    public UserAdapter(List<User> userList, Context context, LayoutInflater li) {
        this.userList = userList;
        this.context = context;
        this.li = li;
    }
    @NonNull
    public ViewHolderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.item_user, parent, false);
        return new ViewHolderVH(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolderVH holder, int position) {

        User user = userList.get(position);

        holder.nombre.setText(user.getUsername());
        holder.email.setText(user.getEmail());
       // holder.rol.setText(user.getRole());
        boolean isOrganizer = user.getRole().equals("Organizador");
        holder.switchRole.setOnCheckedChangeListener(null);
        holder.switchRole.setChecked(isOrganizer);
        holder.tvRoleLabel.setText(isOrganizer ? "Organizador" : "Usuario");
        holder.pbLoading.setVisibility(View.GONE);
        holder.switchRole.setEnabled(true);


        //Logica para cambiar el rol
        holder.switchRole.setOnCheckedChangeListener((buttonView, isChecked) -> {
            holder.tvRoleLabel.setText(isChecked ? "Organizador" : "Usuario");
            int newRoleValue = isChecked ? 1 : 0;

            //actualizar la vista:
            holder.tvRoleLabel.setText(isChecked ? "Organizador" : "Usuario");

            String token = ApiClient.leerToken(context);
            if(token != null && !token.isEmpty()){
                ApiClient.MisEndPoints api = ApiClient.getEndPoints();
                Call<ResponseBody> call = api.updateUserRole(user.getId(), token, newRoleValue);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Rol actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            String msg = "Error al actualizar rol";
                            Toast.makeText(context, msg + " (" + response.code() + ")", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                Log.e("salida", " desde adapter usuario: token es nulo o vacío");
            }

        });

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = ApiClient.leerToken(context);
                if(token != null && !token.isEmpty()){
                    ApiClient.MisEndPoints api = ApiClient.getEndPoints();

                    Call <ResponseBody> call = api.deleteUser(token, user.getId());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = "Error al eliminar el usuario";
                                Toast.makeText(context, msg + " (" + response.code() + ")", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(context, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });



                } else {
                    Log.e("salida", " desde adapter usuario: token es nulo o vacío");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolderVH extends RecyclerView.ViewHolder {
        TextView nombre, email, tvRoleLabel;
        com.google.android.material.switchmaterial.SwitchMaterial switchRole;
        ProgressBar pbLoading;
        Button btnEliminar;


        public ViewHolderVH(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.tvUserName);
            email = itemView.findViewById(R.id.tvUserEmail);
            switchRole = itemView.findViewById(R.id.switchRole);
            tvRoleLabel = itemView.findViewById(R.id.tvRoleLabel);
            pbLoading = itemView.findViewById(R.id.pbLoading);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

}
