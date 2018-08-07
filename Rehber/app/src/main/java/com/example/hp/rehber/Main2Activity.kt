package com.example.hp.rehber

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.lang.reflect.Executable
import java.sql.SQLException

class Main2Activity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        val nameArray = ArrayList<String>()
        val surnameArray = ArrayList<String>()
        val emailArray = ArrayList<String>()
        val phoneArray = ArrayList<String>()
        val imageArray = ArrayList<Bitmap>()


        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,nameArray)
        listView.adapter = arrayAdapter

        try {
            val database = this.openOrCreateDatabase("Rehber", Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS rehber(name VARCHAR, surname VARCHAR, email VARCHAR, phone VARCHAR, image BLOB)")
            val cursor = database.rawQuery("SELECT * FROM rehber ORDER BY name", null)

            val nameIndex = cursor.getColumnIndex("name")
            val surnameIndex = cursor.getColumnIndex("surname")
            val emailIndex = cursor.getColumnIndex("email")
            val phoneIndex = cursor.getColumnIndex("phone")
            val imageIndex = cursor.getColumnIndex("image")
            cursor.moveToFirst()

             while (cursor !=null ){

                nameArray.add(cursor.getString(nameIndex))
                surnameArray.add(cursor.getString(surnameIndex))
                emailArray.add(cursor.getString(emailIndex))
                phoneArray.add(cursor.getString(phoneIndex))

                val byteArray = cursor.getBlob(imageIndex)
               val image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)

               imageArray.add(image)
                arrayAdapter.notifyDataSetChanged()
                 cursor.moveToNext()
            }

             cursor?.close()

        }catch (e: Exception){
            e.printStackTrace()
        }
        //listeden her hangi birşeye tıklanınca hangi activity'e gitmesi gerektiğini gösteriyoruz.
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->

            val intent = Intent(applicationContext,MainActivity::class.java)
            intent.putExtra("name",nameArray[i])
            intent.putExtra("surname",surnameArray[i])
            intent.putExtra("email",emailArray[i])
            intent.putExtra("phone",phoneArray[i])

            //Globals sınıfından chosenIma
            val chosen = Globals.Chosen
            chosen.chosenImage = imageArray[i]
            intent.putExtra("info","old") // daha önce kaydedilen bilgiler görüneceği için old info olduğunu belirtir
            startActivity(intent)
        }
    }

    fun add(view: View){
        val intent = Intent(applicationContext,MainActivity::class.java)
        intent.putExtra("info","new")//add işlemi yaptığı zaman info'nun new olacağını belirtir
        startActivity(intent)
    }
    //Clear All yapmak için oluşturulan menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.clear_all,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==R.id.clear_all){

            val database = this.openOrCreateDatabase("Rehber", Context.MODE_PRIVATE, null)
            database.execSQL("DELETE FROM rehber")
            val intent = Intent(applicationContext,Main2Activity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }


}
