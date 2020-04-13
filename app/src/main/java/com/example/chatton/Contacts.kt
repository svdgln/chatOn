package com.example.chatton

class Contacts {

    public fun Contacts() {

    }

    private lateinit var name:String
    private lateinit var status:String
    private lateinit var image:String

    constructor(name: String, status: String, image: String) {
        this.name = name
        this.status = status
        this.image = image
    }

    fun getName(): String {
        return name;
    }

    fun setName(name:String) {
        this.name=name
    }

    fun getStatus(): String {
        return status;
    }

    fun setStatus(status: String) {
        this.status=status
    }

    fun getImage(): String {
        return image;
    }

    fun setImage(image: String) {
        this.image=image
    }

}