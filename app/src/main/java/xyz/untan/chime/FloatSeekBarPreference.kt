package xyz.untan.chime

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.SeekBar
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class FloatSeekBarPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    Preference(context, attrs, defStyleAttr, defStyleRes), SeekBar.OnSeekBarChangeListener {

    var value: Float
        // Seekbarから値を取得する
        get() = (seekbar?.progress?.times(valueSpacing) ?: 0F) + minValue
        set(v) {
            newValue = v
            persistFloat(v) // SharedPreferenceに保存
            notifyChanged() // UIの更新を要求
        }

    private val minValue: Float
    private val maxValue: Float
    private val valueSpacing: Float // 指定できる値の間隔
    private val format: String // 値のフォーマット

    private var seekbar: SeekBar? = null
    private var textView: TextView? = null

    private var defaultValue = 0F // XMLで設定したデフォルト値
    private var newValue = 0F

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.seekBarPreferenceStyle)
    constructor(context: Context) : this(context, null)

    init {
        widgetLayoutResource = R.layout.pref_float_seekbar // カスタムレイアウトを指定する

        // XMLの値を取得
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FloatSeekBarPreference, defStyleAttr, defStyleRes)
        minValue = ta.getFloat(R.styleable.FloatSeekBarPreference_minValue, 0F)
        maxValue = ta.getFloat(R.styleable.FloatSeekBarPreference_maxValue, 1F)
        valueSpacing = ta.getFloat(R.styleable.FloatSeekBarPreference_valueSpacing, .1F)
        format = ta.getString(R.styleable.FloatSeekBarPreference_format) ?: "%3.1f"
        ta.recycle()
    }

    // Called when a Preference is being inflated and the default value attribute needs to be read. Since different
    // Preference types have different value types, the subclass should get and return the default value which will be
    // its value type.
    override fun onGetDefaultValue(ta: TypedArray?, i: Int): Any {
        defaultValue = ta!!.getFloat(i, 0F) // XMLで指定したデフォルト値を取得
        return defaultValue
    }

    // Implement this to set the initial value of the Preference.
    override fun onSetInitialValue(initValue: Any?) {
        newValue = getPersistedFloat(
            if (initValue is Float) initValue
            else this.defaultValue
        )
    }

    // onGetDefaultValue -> onSetInitialValue -> onBindViewHolder の順に呼ばれる
    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        holder!!.itemView.isClickable = false
        seekbar = holder.findViewById(R.id.seekbar) as SeekBar
        textView = holder.findViewById(R.id.seekbar_value) as TextView

        seekbar!!.setOnSeekBarChangeListener(this)
        // SeekbarはIntのみ受け付けるのでよしなにやる
        seekbar!!.max = ((maxValue - minValue) / valueSpacing).toInt()
        seekbar!!.progress = ((newValue - minValue) / valueSpacing).toInt()
        seekbar!!.isEnabled = isEnabled

        textView!!.text = format.format(newValue)
    }

    // for OnSeekBarChangeListener
    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser) return
        val v = minValue + progress * valueSpacing
        textView!!.text = format.format(v)
    }

    // for OnSeekBarChangeListener
    override fun onStartTrackingTouch(seekbar: SeekBar?) {}

    // for OnSeekBarChangeListener
    override fun onStopTrackingTouch(seekbar: SeekBar?) {
        val v = minValue + seekbar!!.progress * valueSpacing
        persistFloat(v)
    }
}