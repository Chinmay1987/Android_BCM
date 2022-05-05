// TODO: Move to same package as MyApplication
package com.pwc.banking

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.backbase.android.retail.journey.app.us.UsAppActivity
import com.backbase.android.retail.journey.app.us.UsUseCaseDefinitions
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import org.koin.core.context.KoinContextHandler
import org.koin.core.definition.Definition
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

class MyApplicationTest {

    private val expectedQualifiers = mapOf<KClass<*>, Qualifier>()

    /**
     * Launch the app to verify that [UsUseCaseDefinitions] are set up and can be resolved.
     */
    @Ignore("Flaky test always failing on Jenkins")
    @Test fun whenApplicationLaunched_resolvesAllUseCaseDefinitions() {
        // Launch the Activity and use Espresso to wait for its initialization to ensure the application fully
        //  initializes in the splash screen:
        ActivityScenario.launch(UsAppActivity::class.java)
        onView(isRoot()).perform(click())

        // Using reflection, ensure every Definition<T> in UseCaseDefinitions resolves at runtime:
        val koin = KoinContextHandler.get()
        UsUseCaseDefinitions::class.java.forEachDefinitionReturnClass { returnClass ->
            val qualifier = expectedQualifiers[returnClass]

            var useCase: Any? = null
            // Not sure which scope a given returnClass will be in, so try to resolve it in every scope:
            for (scopeDefinition in koin._scopeRegistry.scopeDefinitions.values) {
                val scope = koin.getOrCreateScope(
                    scopeId = scopeDefinition.toString(),
                    qualifier = scopeDefinition.qualifier
                )
                useCase = scope.getOrNull<Any>(returnClass, qualifier)

                // If it was resolved then the assertNotNull will already pass, so break from this inner loop:
                if (useCase != null) break
            }
            assertNotNull(
                "Could not resolve $returnClass. Either it or one of its dependencies has not been added to Koin.",
                useCase
            )
        }
    }

    /**
     * Reflectively finds each [Definition] property of [UsUseCaseDefinitions], then
     */
    private fun Class<UsUseCaseDefinitions>.forEachDefinitionReturnClass(action: (KClass<out Any>) -> Unit) {
        // Find all methods of UsUseCaseDefinitions which take no parameters and return Definition<T>:
        val definitionGetters = methods.filter { method ->
            val isFunction2Getter = method.parameterCount == 0 && method.returnType == Function2::class.java
            if (isFunction2Getter) {
                val genericReturnTypeArgs = (method.genericReturnType as ParameterizedType).actualTypeArguments
                val isDefinition = genericReturnTypeArgs.size == 3 &&
                        genericReturnTypeArgs[0] == Scope::class.java &&
                        genericReturnTypeArgs[1] == DefinitionParameters::class.java
                isDefinition
            } else {
                false
            }
        }

        // For each Definition<T> getter, invoke `action` with T::class
        definitionGetters.forEach { definitionGetter ->
            @Suppress("MoveVariableDeclarationIntoWhen")
            val returnType = (definitionGetter.genericReturnType as ParameterizedType).actualTypeArguments[2]
            val returnClass = when (returnType) {
                is Class<out Any> -> returnType
                is ParameterizedType -> returnType.rawType as Class<out Any>
                else -> throw RuntimeException("Not sure how to handle <$returnType>")
            }
            action.invoke(returnClass.kotlin)
        }
    }
}
