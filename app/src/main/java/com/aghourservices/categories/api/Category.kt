package com.aghourservices.categories.api

import com.aghourservices.R
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Category(
    @PrimaryKey
    var id: Int = 0,
    var icon: String? = null,
    var name: String? = null,
) : RealmObject() {
    object Categories {
        private val images = intArrayOf(
            R.mipmap.doctors,
            R.mipmap.restauraunt,
            R.mipmap.transportation,
            R.mipmap.pharmacy,
            R.mipmap.teachers,
            R.mipmap.x_rays,
            R.mipmap.market,
            R.mipmap.tools,
            R.mipmap.craftsmen,
            R.mipmap.metal,
            R.mipmap.clothes,
            R.mipmap.vet_doc,
            R.mipmap.eng,
            R.mipmap.electronics,
            R.mipmap.`fun`,
            R.mipmap.women,
            R.mipmap.lawyer,
            R.mipmap.library,
            R.mipmap.birds,
            R.mipmap.people,
        )
        private val categories_name = arrayOf(
            "الأطباء",
            "مطاعم وأكل",
            "النقل والمواصلات",
            "صيدليات",
            "مدرسين",
            "أشعة وتحاليل طبية",
            "ماركت",
            "أدوات وأجهزة منزلية",
            "الحرفيين",
            "حدايد وأدوات صحية",
            "ملابس وأحذية",
            "الأطباء البيطريون",
            "إستشارات هندسية",
            "الإلكترونيات",
            "الترفيه والنوادي",
            "المرأة - الصحة والجمال",
            "محامين",
            "المكتبات",
            "عالم الطيور",
            "شخصيات عامة",
        )
        var List: ArrayList<Category>? = null
            get() {
                if (field != null)
                    return field
                field = ArrayList()
                for (i in images.indices) {
                    val imageId = images[i]
                    val categoryName = categories_name[i]

                    val category = Category(0, imageId.toString(), categoryName)
                    field!!.add(category)
                }
                return field
            }
    }
}
