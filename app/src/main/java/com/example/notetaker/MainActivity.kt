package com.example.notetaker

import android.app.Activity
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.notetaker.database.Word
import com.example.notetaker.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val newWordActivityRequestCode = 1

    private lateinit var binding : ActivityMainBinding


   private  val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.wordViewModel = wordViewModel
        binding.lifecycleOwner = this







        wordViewModel.allWords.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let {
                var arrList = mutableListOf<String>("")
                for (word in words) {
                    arrList.add(word.word)
                }
                binding.textView.text = arrList.joinToString(separator = "\n")


            }
        })

        binding.fab.setOnClickListener {
            val intent = Intent(this, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)

        }
        binding.delete.setOnClickListener {

            if (wordViewModel.allWords.value?.isNotEmpty() == true) {
                wordViewModel.delete()

            }
            else Toast.makeText(applicationContext,
                    "No Items For Delete",
                    Toast.LENGTH_LONG).show()


        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.i("RequestCode", requestCode.toString())
        Log.i("RequestCode", resultCode.toString())

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let {
                val word = Word(it)
                wordViewModel.insert(word)

           }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }

}