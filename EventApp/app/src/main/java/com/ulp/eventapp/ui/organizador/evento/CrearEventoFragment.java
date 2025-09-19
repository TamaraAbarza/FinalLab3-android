package com.ulp.eventapp.ui.organizador.evento;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ulp.eventapp.R;
import com.ulp.eventapp.databinding.FragmentCrearEventoBinding;
import com.ulp.eventapp.model.Event;
import com.ulp.eventapp.request.ApiClient;

import java.util.Calendar;
import java.util.Locale;

public class CrearEventoFragment extends Fragment {

    private FragmentCrearEventoBinding binding;
    private CrearEventoViewModel vm;
    private ActivityResultLauncher<Intent> arl;
    private Intent intent;
    private Uri uriFoto;
    private Event evento = null;

    private Calendar calendar = Calendar.getInstance();

    public static CrearEventoFragment newInstance() {
        return new CrearEventoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(CrearEventoViewModel.class);
        binding = FragmentCrearEventoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        abrirGaleria();

        // PICKERS
        binding.etFechaEvento.setFocusable(false);
        binding.etFechaEvento.setOnClickListener(v -> mostrarDatePicker());

        binding.etHoraEvento.setFocusable(false);
        binding.etHoraEvento.setOnClickListener(v -> mostrarTimePicker());

        // Si el adapter vino con bundle, recuperalo
        Bundle args = getArguments();
        if (args != null) {
            vm.recuperarEvento(args);
        }

        vm.getmVerificar().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), "¡Operación exitosa!", Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(CrearEventoFragment.this).navigateUp();
            }
        });

        // setea los campos si vino a editar
        vm.getMutableEvento().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                evento = event;
                binding.btnRegistrarE.setText("Modificar evento");

                //Fecha y hora
                if (event.getDate() != null) {
                    String dateTime = event.getDate().trim();

                    if (dateTime.contains("T")) {
                        String[] partes = dateTime.split("T");
                        String fecha = partes[0]; // yyyy-MM-dd
                        String hora = partes[1].substring(0, 5); // HH:mm
                        binding.etFechaEvento.setText(fecha);
                        binding.etHoraEvento.setText(hora);
                    } else if (dateTime.contains(" ")) {
                        String[] partes = dateTime.split(" ");
                        if (partes.length >= 2) {
                            binding.etFechaEvento.setText(partes[0]);
                            binding.etHoraEvento.setText(partes[1].substring(0, 5));
                        } else {
                            binding.etFechaEvento.setText(dateTime);
                        }
                    } else {
                        binding.etFechaEvento.setText(dateTime);
                    }
                }

                ImageView foto = binding.ivFoto;
                binding.etNombreEvento.setText(event.getName());
                binding.etUbicacionEvento.setText(event.getLocation());
                binding.etDescripcionEvento.setText(event.getDescription());

                String imageUrl = ApiClient.URL + event.getImageUrl();

                // Usa Glide para cargar la imagen
                Glide.with(CrearEventoFragment.this)
                        .load(imageUrl)
                        //.placeholder(R.drawable.placeholder)
                        .error(R.drawable.ic_error)
                        .into(foto);

                binding.ivFoto.setVisibility(View.VISIBLE);
                binding.tvAddPhoto.setVisibility(View.GONE);
                binding.tvSubPhoto.setVisibility(View.GONE);

            } else {
                evento = null;
                binding.btnRegistrarE.setText("Crear evento");
                binding.etNombreEvento.setText("");
                binding.etFechaEvento.setText("");
                binding.etHoraEvento.setText("");
                binding.etUbicacionEvento.setText("");
                binding.etDescripcionEvento.setText("");
            }
        });


        vm.getUriMutable().observe(getViewLifecycleOwner(), uri -> {
            binding.ivFoto.setImageURI(uri);
            uriFoto = uri;

            binding.ivFoto.setVisibility(View.VISIBLE);
            binding.tvAddPhoto.setVisibility(View.GONE);
            binding.tvSubPhoto.setVisibility(View.GONE);
        });

        binding.btnFoto.setOnClickListener(v -> arl.launch(intent));

        binding.btnRegistrarE.setOnClickListener(v -> {
            String name = binding.etNombreEvento.getText().toString();
            String fecha = binding.etFechaEvento.getText().toString();
            String hora = binding.etHoraEvento.getText().toString();
            String location = binding.etUbicacionEvento.getText().toString();
            String description = binding.etDescripcionEvento.getText().toString();

            if (name.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                Toast.makeText(getContext(), "Nombre, fecha y hora son obligatorios.", Toast.LENGTH_SHORT).show();
                return;
            }

            String dateTime = fecha + " " + hora;

            if (evento != null && evento.getId() > 0) {
                evento.setName(name);
                evento.setDate(dateTime);
                evento.setLocation(location);
                evento.setDescription(description);
                vm.actualizarEvento(evento.getId(), evento, uriFoto);
            } else {
                Event nuevo = new Event(name, dateTime, location, description);
                vm.registrarEvento(nuevo, uriFoto);
            }
        });

        return root;
    }

    private void mostrarDatePicker() {
        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (month + 1), dayOfMonth);
                    binding.etFechaEvento.setText(fecha);
                },
                anio, mes, dia
        );
        datePicker.show();
    }

    private void mostrarTimePicker() {
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    String horaStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    binding.etHoraEvento.setText(horaStr);
                },
                hora, minuto, true
        );
        timePicker.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void abrirGaleria() {
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> vm.recibirFoto(result));
    }
}
