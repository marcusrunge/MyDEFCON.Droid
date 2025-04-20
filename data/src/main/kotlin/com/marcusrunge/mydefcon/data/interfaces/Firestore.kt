package com.marcusrunge.mydefcon.data.interfaces

interface Firestore{
    /**
     * Test connection to Firestore
     */
    suspend fun testConnection(): Boolean
}