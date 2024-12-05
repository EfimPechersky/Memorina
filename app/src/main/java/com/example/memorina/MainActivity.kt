package com.example.memorina

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartGameDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
            builder.setMessage("You Won!")
                .setPositiveButton("Ok") { dialog, id ->
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
class MainActivity : AppCompatActivity() {
    var wincondition=0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(applicationContext)
        layout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.weight = 1.toFloat() // единичный вес
        val rows = Array(4, { LinearLayout(applicationContext)})
        val cardViews = ArrayList<ImageView>()
        val tags= mutableListOf("card1","card2","card3","card4","card5","card6","card7","card8")
        for (i in 1..16) {
            cardViews.add( // вызываем конструктор для создания нового ImageView
                ImageView(applicationContext).apply {
                    layoutParams = params
                    tag=tags[i%8]
                    setImageResource(R.drawable.rubash)
                    setOnClickListener(colorListener)
                })
        }



        var count = 0
        for (view in cardViews.shuffled()) {
            val row: Int = count / 4
            rows[row].addView(view)
            count ++
        }
        for (row in rows) {
            layout.addView(row)
        }
        setContentView(layout)
    }
    var opened_cards=ArrayList<String>();

    suspend fun setBackgroundWithDelay(v: View) {
        if (opened_cards.size<2){
            openCards(v)
            v.isClickable = false
            while (opened_cards.size<2){
                delay(10)
            }
            if (opened_cards[0]==opened_cards[1]) {
                delay(700)
                v.visibility = View.INVISIBLE
                wincondition+=1
                delay(100)
                if (wincondition>=16){
                    wincondition=0
                    StartGameDialogFragment().show(supportFragmentManager, "GAME_DIALOG")
                    recreate();
                }
                opened_cards.clear()
            }else{
                delay(700)
                (v as ImageView).setImageResource(R.drawable.rubash)
                opened_cards.clear()
                v.isClickable = true
            }

        }
    }

    suspend fun openCards(v:View) {
        opened_cards.add(v.tag.toString())
        when (v.tag) {
            "card1" -> (v as ImageView).setImageResource(R.drawable.card1)
            "card2" -> (v as ImageView).setImageResource(R.drawable.card2)
            "card3" -> (v as ImageView).setImageResource(R.drawable.card3)
            "card4" -> (v as ImageView).setImageResource(R.drawable.card4)
            "card5" -> (v as ImageView).setImageResource(R.drawable.card5)
            "card6" -> (v as ImageView).setImageResource(R.drawable.card6)
            "card7" -> (v as ImageView).setImageResource(R.drawable.card7)
            "card8" -> (v as ImageView).setImageResource(R.drawable.card8)
        }
    }

    // обработчик нажатия на кнопку
    val colorListener = View.OnClickListener() {
        // запуск функции в фоновом потоке
        GlobalScope.launch (Dispatchers.Main)
        { setBackgroundWithDelay(it) }
    }
}