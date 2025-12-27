package uk.ac.tees.mad.e4617552.saferoute.data

data class SosAlert(
    val userId: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)