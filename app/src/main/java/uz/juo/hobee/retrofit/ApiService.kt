package uz.juo.hobee.retrofit

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import uz.juo.hobee.models.*

interface ApiService {

    @GET("medicaments/best")
    suspend fun getBestMedicaments(): List<Medicament>

    @GET("medicaments/{id}")
    suspend fun getMedicamentById(@Path("id") id: Int): GetById

    @GET("branches/{id}")
    suspend fun getPharmacyById(@Path("id") id: Int): Item

    @POST("medicaments")
    suspend fun getMedicationByIdArray(@Query("list") list: ArrayList<Int>): Medicament

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
        @Query("name") name: String
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

    @GET("branches")
    suspend fun getAllBranches(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("name") name: String,
        @Query("latitude") lat: String,
        @Query("longitude") long: String
    ): AllBranches

    @GET("branches")

    suspend fun getAllManufacturer(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("name") name: String
    ): AllBranches
}
