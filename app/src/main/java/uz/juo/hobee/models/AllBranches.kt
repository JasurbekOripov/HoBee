package uz.juo.hobee.models

import java.io.Serializable

data class AllBranches(
    val count: Int,
    val items: List<Item>
):Serializable