package org.radioplayer.automotive.designsystem.components.primitives.flag

import androidx.annotation.DrawableRes
import org.radioplayer.automotive.designsystem.R

/**
 * Available flags, identified by ISO 3166-1 alpha-2 [countryCode].
 * Use [fromCountryCode] to map a code coming from data; unknown codes resolve to [Placeholder].
 */
enum class FlagSetEnum(
    val countryCode: String?,
    @param:DrawableRes private val small: Int,
    @param:DrawableRes private val default: Int,
    @param:DrawableRes private val large: Int,
) {
    Placeholder(
        null,
        R.drawable.country_placeholder__size_small,
        R.drawable.country_placeholder__size_default,
        R.drawable.country_placeholder__size_large
    ),
    Austria(
        "AT",
        R.drawable.country_austria__size_small,
        R.drawable.country_austria__size_default,
        R.drawable.country_austria__size_large
    ),
    Belgium(
        "BE",
        R.drawable.country_belgium__size_small,
        R.drawable.country_belgium__size_default,
        R.drawable.country_belgium__size_large
    ),
    Canada(
        "CA",
        R.drawable.country_canada__size_small,
        R.drawable.country_canada__size_default,
        R.drawable.country_canada__size_large
    ),
    Croatia(
        "HR",
        R.drawable.country_croatia__size_small,
        R.drawable.country_croatia__size_default,
        R.drawable.country_croatia__size_large
    ),
    Cyprus(
        "CY",
        R.drawable.country_cyprus__size_small,
        R.drawable.country_cyprus__size_default,
        R.drawable.country_cyprus__size_large
    ),
    Denmark(
        "DK",
        R.drawable.country_denmark__size_small,
        R.drawable.country_denmark__size_default,
        R.drawable.country_denmark__size_large
    ),
    Estonia(
        "EE",
        R.drawable.country_estonia__size_small,
        R.drawable.country_estonia__size_default,
        R.drawable.country_estonia__size_large
    ),
    Finland(
        "FI",
        R.drawable.country_finland__size_small,
        R.drawable.country_finland__size_default,
        R.drawable.country_finland__size_large
    ),
    France(
        "FR",
        R.drawable.country_france__size_small,
        R.drawable.country_france__size_default,
        R.drawable.country_france__size_large
    ),
    Germany(
        "DE",
        R.drawable.country_germany__size_small,
        R.drawable.country_germany__size_default,
        R.drawable.country_germany__size_large
    ),
    Greece(
        "GR",
        R.drawable.country_greece__size_small,
        R.drawable.country_greece__size_default,
        R.drawable.country_greece__size_large
    ),
    Ireland(
        "IE",
        R.drawable.country_ireland__size_small,
        R.drawable.country_ireland__size_default,
        R.drawable.country_ireland__size_large
    ),
    Italy(
        "IT",
        R.drawable.country_italy__size_small,
        R.drawable.country_italy__size_default,
        R.drawable.country_italy__size_large
    ),
    Liechtenstein(
        "LI",
        R.drawable.country_liechtenstein__size_small,
        R.drawable.country_liechtenstein__size_default,
        R.drawable.country_liechtenstein__size_large
    ),
    Luxembourg(
        "LU",
        R.drawable.country_luxembourg__size_small,
        R.drawable.country_luxembourg__size_default,
        R.drawable.country_luxembourg__size_large
    ),
    Netherlands(
        "NL",
        R.drawable.country_netherlands__size_small,
        R.drawable.country_netherlands__size_default,
        R.drawable.country_netherlands__size_large
    ),
    Norway(
        "NO",
        R.drawable.country_norway__size_small,
        R.drawable.country_norway__size_default,
        R.drawable.country_norway__size_large
    ),
    Portugal(
        "PT",
        R.drawable.country_portugal__size_small,
        R.drawable.country_portugal__size_default,
        R.drawable.country_portugal__size_large
    ),
    Serbia(
        "RS",
        R.drawable.country_serbia__size_small,
        R.drawable.country_serbia__size_default,
        R.drawable.country_serbia__size_large
    ),
    Slovenia(
        "SI",
        R.drawable.country_slovenia__size_small,
        R.drawable.country_slovenia__size_default,
        R.drawable.country_slovenia__size_large
    ),
    Spain(
        "ES",
        R.drawable.country_spain__size_small,
        R.drawable.country_spain__size_default,
        R.drawable.country_spain__size_large
    ),
    Sweden(
        "SE",
        R.drawable.country_sweden__size_small,
        R.drawable.country_sweden__size_default,
        R.drawable.country_sweden__size_large
    ),
    Switzerland(
        "CH",
        R.drawable.country_swizterland__size_small,
        R.drawable.country_swizterland__size_default,
        R.drawable.country_swizterland__size_large
    ),
    UnitedKingdom(
        "GB",
        R.drawable.country_unitedkingdom__size_small,
        R.drawable.country_unitedkingdom__size_default,
        R.drawable.country_unitedkingdom__size_large
    ),
    UnitedStates(
        "US",
        R.drawable.country_united_states__size_small,
        R.drawable.country_united_states__size_default,
        R.drawable.country_united_states__size_large
    );

    @DrawableRes
    fun resId(size: FlagSize): Int = when (size) {
        FlagSize.Small -> small
        FlagSize.Default -> default
        FlagSize.Large -> large
    }

    companion object {
        private val byCountryCode: Map<String, FlagSetEnum> =
            entries.mapNotNull { flag -> flag.countryCode?.let { it to flag } }.toMap()

        /** Unknown or missing codes fall back to [Placeholder]. */
        fun fromCountryCode(code: String?): FlagSetEnum =
            code?.let { byCountryCode[it.uppercase()] } ?: Placeholder
    }
}
