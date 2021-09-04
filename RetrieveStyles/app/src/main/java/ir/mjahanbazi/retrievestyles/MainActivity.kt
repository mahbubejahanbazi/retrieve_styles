package ir.mjahanbazi.retrievestyles

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val customButton: CustomButton = findViewById(R.id.button1)
        customButton.setOnClickListener {
            Toast.makeText(this, "button is clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
