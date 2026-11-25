package br.com.grafica.calculadora.nucleo.tema

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import calculadora.sharedui.generated.resources.Res
import calculadora.sharedui.generated.resources.bricolage_grotesque_bold
import calculadora.sharedui.generated.resources.bricolage_grotesque_extrabold
import calculadora.sharedui.generated.resources.bricolage_grotesque_medium
import calculadora.sharedui.generated.resources.bricolage_grotesque_regular
import calculadora.sharedui.generated.resources.bricolage_grotesque_semibold
import calculadora.sharedui.generated.resources.plus_jakarta_sans_bold
import calculadora.sharedui.generated.resources.plus_jakarta_sans_medium
import calculadora.sharedui.generated.resources.plus_jakarta_sans_regular
import calculadora.sharedui.generated.resources.plus_jakarta_sans_semibold
import org.jetbrains.compose.resources.Font

@Composable
fun familiaFonteBricolage(): FontFamily = FontFamily(
    Font(Res.font.bricolage_grotesque_regular, FontWeight.Normal),
    Font(Res.font.bricolage_grotesque_medium, FontWeight.Medium),
    Font(Res.font.bricolage_grotesque_semibold, FontWeight.SemiBold),
    Font(Res.font.bricolage_grotesque_bold, FontWeight.Bold),
    Font(Res.font.bricolage_grotesque_extrabold, FontWeight.ExtraBold)
)

@Composable
fun familiaFontePlusJakarta(): FontFamily = FontFamily(
    Font(Res.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(Res.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(Res.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(Res.font.plus_jakarta_sans_bold, FontWeight.Bold)
)

@Composable
fun tipografiaAtelie(): Typography {
    val bricolage = familiaFonteBricolage()
    val jakarta = familiaFontePlusJakarta()

    return Typography(
        displayLarge = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp
        ),
        displayMedium = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 38.sp
        ),
        displaySmall = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            lineHeight = 34.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 32.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.SemiBold,
            fontSize = 19.sp,
            lineHeight = 25.sp
        ),
        titleLarge = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),
        titleMedium = TextStyle(
            fontFamily = jakarta,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        titleSmall = TextStyle(
            fontFamily = jakarta,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = jakarta,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = jakarta,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodySmall = TextStyle(
            fontFamily = jakarta,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        labelLarge = TextStyle(
            fontFamily = jakarta,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),
        labelMedium = TextStyle(
            fontFamily = jakarta,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        labelSmall = TextStyle(
            fontFamily = jakarta,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp
        )
    )
}
