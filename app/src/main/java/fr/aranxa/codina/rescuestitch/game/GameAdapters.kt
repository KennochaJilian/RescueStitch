package fr.aranxa.codina.rescuestitch.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.Element
import fr.aranxa.codina.rescuestitch.dataClasses.ElementValueType

class ElementOnClickListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(id: Int) = clickListener(id)
}

class ButtonsOperationAdapter(
    val context: Context,
    var buttons: List<Element>,
    private val clickListener: ElementOnClickListener

) : RecyclerView.Adapter<ButtonsOperationAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button = view.findViewById<ImageView>(R.id.button_operation_item)
        val textButton = view.findViewById<TextView>(R.id.button_panel_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.button_operation_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val button = buttons[position]
        when (button.valueType) {
            ElementValueType.color.toString() -> {
                holder.button.setBackgroundColor(
                    Color.parseColor(
                        button.value
                    )
                )
                holder.textButton.visibility = INVISIBLE
            }
            ElementValueType.string.toString() -> setButtonText(holder, button.value)
            ElementValueType.int.toString() -> setButtonText(holder, button.value)
            ElementValueType.float.toString() -> setButtonText(holder, button.value)
        }
        holder.button.id = button.id

        holder.button.setOnClickListener { clickListener.onClick(button.id) }
    }

    override fun getItemCount(): Int = buttons.size

    fun setButtonText(holder: ViewHolder, text: String) {
        holder.textButton.text = text
        holder.button.setBackgroundColor(
            Color.parseColor(
                "#2972b6"
            )
        )
    }

}

class SwitchesOperationAdapter(
    val context: Context,
    var switches: List<Element>,
    private val clickListener: ElementOnClickListener

) : RecyclerView.Adapter<SwitchesOperationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val switch = view.findViewById<Switch>(R.id.switch_operation_item)
        val textValue = view.findViewById<TextView>(R.id.switch_value_text_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.switch_operation_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val switch = switches[position]
        when (switch.valueType) {
            ElementValueType.color.toString() -> holder.switch.setBackgroundColor(
                Color.parseColor(
                    switch.value
                )
            )
            ElementValueType.string.toString() -> {
                holder.textValue.text = switch.value
                holder.textValue.visibility = VISIBLE

            }
            ElementValueType.int.toString() -> {
                holder.textValue.text = switch.value
                holder.textValue.visibility = VISIBLE
            }
            ElementValueType.float.toString() -> {
                holder.textValue.text = switch.value
                holder.textValue.visibility = VISIBLE
            }
        }
        holder.switch.id = switch.id
        holder.switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                clickListener.onClick(switch.id)
            }
        }
    }

    override fun getItemCount(): Int = switches.size
}