package com.aghourservices.user.addData

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aghourservices.databinding.ActivityAddDataBinding

class AddDataActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}