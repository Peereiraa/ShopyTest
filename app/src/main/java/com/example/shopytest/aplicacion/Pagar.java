package com.example.shopytest.aplicacion;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopytest.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Calendar;

public class Pagar extends AppCompatActivity {

    private static final String TAG = "Pagar";

    private ImageView pagarPaypal;
    private static final int PAYPAL_REQUEST_CODE = 123;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // Cambia a ENVIRONMENT_PRODUCTION para producción
            .clientId("AUka40W-TkB5hq6kGy_m8nprwWVAjO_Dx-Uq2RKjHGIcS7f8ECNhV1vRuKh5MmwzO9jglEWJrx8uqKxt"); // Reemplaza con tu Client ID de PayPal

    private TextInputEditText numeroTarjetaEditText, nombreTitularEditText, fechaCaducidadEditText, cvcEditText;
    private TextInputLayout numeroTarjetaLayout, nombreTitularLayout, fechaCaducidadLayout, cvcLayout;
    private Button pagarButton, cancelarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagar);

        // Iniciar el servicio de PayPal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        numeroTarjetaLayout = findViewById(R.id.numeroTarjetaLayout);
        nombreTitularLayout = findViewById(R.id.nombreTitularLayout);
        fechaCaducidadLayout = findViewById(R.id.fechaCaducidadLayout);
        cvcLayout = findViewById(R.id.cvcLayout);

        numeroTarjetaEditText = findViewById(R.id.numeroTarjetaEditText);
        nombreTitularEditText = findViewById(R.id.nombreTitularEditText);
        fechaCaducidadEditText = findViewById(R.id.fechaCaducidadEditText);
        cvcEditText = findViewById(R.id.cvcEditText);
        cancelarButton = findViewById(R.id.cancelarButton);
        pagarPaypal = findViewById(R.id.pagarPaypal);

        pagarButton = findViewById(R.id.pagarButton);

        // Formateo del número de tarjeta
        numeroTarjetaEditText.addTextChangedListener(new TextWatcher() {
            private static final char SPACE = ' ';
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (SPACE == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(SPACE)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(SPACE));
                    }
                }
            }
        });

        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pagar.this, Carrito.class);
                startActivity(intent);
            }
        });

        // Abrir DatePicker para la fecha de caducidad
        fechaCaducidadEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Pagar.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formattedDate = String.format("%02d/%02d", monthOfYear + 1, year % 100);
                        fechaCaducidadEditText.setText(formattedDate);
                    }
                }, year, month, calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        pagarPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

        // Configurar el botón de pagar
        pagarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
    }

    private void validarDatos() {
        // Validar número de tarjeta
        String numeroTarjeta = numeroTarjetaEditText.getText().toString().trim().replace(" ", "");
        if (numeroTarjeta.length() != 16) {
            numeroTarjetaLayout.setError("El número de tarjeta debe tener 16 dígitos");
            return;
        } else {
            numeroTarjetaLayout.setError(null);
        }

        // Validar nombre del titular
        String nombreTitular = nombreTitularEditText.getText().toString().trim();
        if (TextUtils.isEmpty(nombreTitular)) {
            nombreTitularLayout.setError("Por favor, ingrese el nombre del titular");
            return;
        } else {
            nombreTitularLayout.setError(null);
        }

        // Validar fecha de caducidad
        String fechaCaducidad = fechaCaducidadEditText.getText().toString().trim();
        if (!isValidExpiryDate(fechaCaducidad)) {
            fechaCaducidadLayout.setError("Por favor, ingrese una fecha de caducidad válida (MM/YY)");
            return;
        } else {
            fechaCaducidadLayout.setError(null);
        }

        // Validar CVC
        String cvc = cvcEditText.getText().toString().trim();
        if (cvc.length() != 3) {
            cvcLayout.setError("El CVC debe tener 3 dígitos");
        } else {
            cvcLayout.setError(null);
            Toast.makeText(getApplicationContext(), "Opcion en Desarrollo", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidExpiryDate(String expiryDate) {
        if (expiryDate.length() != 5) {
            return false;
        }
        String[] parts = expiryDate.split("/");
        if (parts.length != 2) {
            return false;
        }
        try {
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            if (month < 1 || month > 12) {
                return false;
            }
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR) % 100; // Solo los dos últimos dígitos del año actual
            return year >= currentYear;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void processPayment() {
        PayPalPayment payment = new PayPalPayment(new BigDecimal("10.00"), "USD", "Sample Item",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        Toast.makeText(getApplicationContext(), "Pago realizado con éxito", Toast.LENGTH_LONG).show();
                        // Puedes manejar la confirmación del pago aquí
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Pago cancelado", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(getApplicationContext(), "Pago inválido o fallido", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}
