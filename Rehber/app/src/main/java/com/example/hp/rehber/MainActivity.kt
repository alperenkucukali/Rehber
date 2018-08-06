package com.example.hp.rehber

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream




class MainActivity : AppCompatActivity() {



    var name1 = ""
    var surname1 = ""
    var email1 = ""
    var phone1 = ""
    var image1 : Bitmap? = null
    var selectedImage : Bitmap? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //arama işlemi
        callButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone1")
            startActivity(intent)
        }
        val intent = intent

        val info = intent.getStringExtra("info")
        if(info.equals("new")){
            val background = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.select)
            imageView.setImageBitmap(background)
            button2.visibility = View.VISIBLE

            editText1.setText("")
            editText2.setText("")
            editText3.setText("")
            editText4.setText("")

        }else{
            button6.visibility = View.VISIBLE
            button3.visibility=View.VISIBLE
            callButton.visibility=View.VISIBLE
            val name= intent.getStringExtra("name")
            editText1.setText(name)
            name1 = name
            val surname= intent.getStringExtra("surname")
            editText2.setText(surname)
            surname1 = surname
            val email= intent.getStringExtra("email")
            editText3.setText(email)
            email1 = email
            val phone= intent.getStringExtra("phone")
            editText4.setText(phone)
            phone1 = phone

            val chosen = Globals.Chosen

            val bitmap = chosen.returnImage()

            imageView.setImageBitmap(bitmap)
            image1 = bitmap

            button2.visibility= View.INVISIBLE

        }
    }

    fun select(view: View){
        // Kullanıcıdan galerisine girmek için izin istiyoruz.
        // izin verirse veya vermezse neler yapması gerektiğini belirtiyoruz.
        //yani if-else yapısı ile yaptığımız şey : fotoğraf albümüne girmek için izin istemek eğer izin var ise fotoğraf albümüne erişmek
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),2)
        }else{
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,1)
        }
    }
            // izin verip vermediğini kontrol ediyoruz
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if(requestCode ==2){
            if(grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,1)

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

        // kullanıcı izin vermiş mi ?
        //izin vermiş ve gerçekten bir veri seçmiştir
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==1 && resultCode == Activity.RESULT_OK && data != null){
            val image = data.data

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver,image)
                imageView.setImageBitmap(selectedImage)

            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun delete(view: View){

        val name = editText1.text.toString()
        val surname = editText2.text.toString()
        val email = editText3.text.toString()
        val phone = editText4.text.toString()

        try {
            val database = this.openOrCreateDatabase("Rehber",Context.MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS rehber(name VARCHAR, surname VARCHAR, email VARCHAR, phone VARCHAR, image BLOB)")

            val sqlString="DELETE FROM rehber WHERE name ='"+ name  +"' and surname='"+surname+"' and email ='"+email+"' and phone='"+phone+"' "
            val statement = database.compileStatement(sqlString)

            statement.execute()

        }catch (e: Exception){
            e.printStackTrace()
        }

        val intent = Intent(applicationContext,Main2Activity::class.java)
        startActivity(intent)

    }
    fun update(view: View):Unit{

        val name = editText1.text.toString()
        val surname = editText2.text.toString()
        val email = editText3.text.toString()
        val phone = editText4.text.toString()

        //image'i dataya çeviriyorum
        val outputStream = ByteArrayOutputStream()
        selectedImage?.compress(Bitmap.CompressFormat.PNG,50,outputStream)
        val byteArray = outputStream.toByteArray()

        try {
            val database = this.openOrCreateDatabase("Rehber",Context.MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS rehber(name VARCHAR, surname VARCHAR, email VARCHAR, phone VARCHAR, image BLOB)")

            val sqlString="UPDATE rehber " +
                    "SET name ='"+ name  +"', surname='"+surname+"', email ='"+email+"' , phone='"+phone+"'   " +
                    " WHERE  name ='"+ name1  +"' and surname='"+surname1+"' and email ='"+email1+"' and phone='"+phone1+"'    "
            val statement = database.compileStatement(sqlString)

            statement.execute()

        }catch (e: Exception){
            e.printStackTrace()
        }
            val intent = Intent(applicationContext,Main2Activity::class.java)
            startActivity(intent)
    }
    fun savePerson(view: View){

        val name = editText1.text.toString()
        val surname = editText2.text.toString()
        val email = editText3.text.toString()
        val phone = editText4.text.toString()

        //image'i dataya çeviriyorum
        val outputStream = ByteArrayOutputStream()
        selectedImage?.compress(Bitmap.CompressFormat.PNG,50,outputStream)
        val byteArray = outputStream.toByteArray()

        try {
            val database = this.openOrCreateDatabase("Rehber",Context.MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS rehber(name VARCHAR, surname VARCHAR, email VARCHAR, phone VARCHAR, image BLOB)")

            val sqlString="INSERT INTO rehber(name, surname, email, phone, image) VALUES(?,?,?,?,?)"
            val statement = database.compileStatement(sqlString)

            statement.bindString(1,name)
            statement.bindString(2,surname)
            statement.bindString(3,email)
            statement.bindString(4,phone)
            statement.bindBlob(5,byteArray)
            statement.execute()

        }catch (e: Exception){
            e.printStackTrace()
        }

        val intent = Intent(applicationContext,Main2Activity::class.java)
        startActivity(intent)

    }

}
