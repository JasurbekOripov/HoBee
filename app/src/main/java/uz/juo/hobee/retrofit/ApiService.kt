package uz.juo.hobee.retrofit

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import uz.juo.hobee.models.*

interface ApiService {

    @GET("medicaments/best")
    suspend fun getBestMedicaments(): List<Medicament>

    @GET("medicaments/{id}")
    suspend fun getMedicamentById(@Path("id") id: Int): Response<GetById>

    @GET("branches/{id}")
    suspend fun getPharmacyById(@Path("id") id: Int): Item

    @POST("medicaments/list")
     fun updateFavorites(@Body list: PostObject): Call<ObjectFavorites>

    @GET("branches/nearest")
    suspend fun getNeariestPharmacy(
        @Query("latitude") lat: String,
        @Query("longitude") long: String
    ): List<NeariestPharmcy>

    @GET("branches/medicament")
    suspend fun getMedicamentsPharmacy(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("name") name: String,
        @Query("branch_id") branch_id: Int
    ): SearchMedicament

    @GET("medicaments")
    suspend fun getMedicamentByName(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("name") name: String,
        @Query("manufacturer") manufacturer: String
    ): SearchMedicament

    @GET("branches/medicament/price")
    suspend fun getPharmacyByMedicamentPrice(
        @Query("latitude") lat: String,
        @Query("longitude") long: String,
        @Query("medicament_id") id: Int,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
    ): BranchesByMedIdPrice

    @GET("branches/medicament/location")
    suspend fun getPharmacyByMedicamentMap(
        @Query("latitude") lat: String,
        @Query("longitude") long: String,
        @Query("medicament_id") id: Int,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
    ): BranchesByMedIdPrice

    @GET("branches/medicament/map")
    suspend fun getBranchesForMap(
        @Query("latitude") lat: String,
        @Query("longitude") long: String,
        @Query("medicament_id") id: Int,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
    ): BranchesByMedIdPrice

    @GET("branches/medicament/map")
    fun branchPriceForMap(
        @Query("latitude") lat: String,
        @Query("longitude") long: String,
        @Query("medicament_id") id: Int
    ): Call<BranchForMap>


    @GET("branches")
    suspend fun getAllBranches(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("name") name: String,
        @Query("latitude") lat: String,
        @Query("longitude") long: String
    ): AllBranches

    @GET("manufacturer")
    suspend fun getAllManufacturer(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("name") name: String
    ): Manufacturer
}
