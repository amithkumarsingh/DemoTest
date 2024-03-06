package com.whitecoats.clinicplus.models

import java.io.Serializable

class FeedsModel : Serializable {
    var total = 0
    var data: MutableList<Data>? = null
    var status = 0
    override fun toString(): String {
        val sb = StringBuilder("FeedsModel{")
        sb.append("total=").append(total)
        sb.append(", data=").append(data)
        sb.append(", status=").append(status)
        sb.append('}')
        return sb.toString()
    }

    class Data : Serializable {
        var id = 0
        var content_title: String? = null
        var content_desc: String? = null
        var content_type: String? = null
        private var distribution_setting = 0
        var active = 0
        var source: String? = null
        var content_path: String? = null
        var content_image: String? = null
        var content_value: String? = null
        var content_created: ContentCreated? = null
        var created_at: String? = null
        var published_on: String? = null
        override fun toString(): String {
            val sb = StringBuilder("Data{")
            sb.append("id=").append(id)
            sb.append(", content_title='").append(content_title).append('\'')
            sb.append(", content_desc='").append(content_desc).append('\'')
            sb.append(", content_type='").append(content_type).append('\'')
            sb.append(", distribution_setting=").append(distribution_setting)
            sb.append(", active=").append(active)
            sb.append(", source='").append(source).append('\'')
            sb.append(", content_path='").append(content_path).append('\'')
            sb.append(", content_created=").append(content_created)
            sb.append(", created_at='").append(created_at).append('\'')
            sb.append(", published_on='").append(published_on).append('\'')
            sb.append('}')
            return sb.toString()
        }

        class ContentCreated : Serializable {
            var name: String? = null
            override fun toString(): String {
                val sb = StringBuilder("ContentCreated{")
                sb.append("name='").append(name).append('\'')
                sb.append('}')
                return sb.toString()
            }
        }
    }
}