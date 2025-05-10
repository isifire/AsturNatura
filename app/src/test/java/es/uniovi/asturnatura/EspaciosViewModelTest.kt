package es.uniovi.asturnatura

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import es.uniovi.asturnatura.data.repository.EspaciosRepository
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.kotlin.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class EspaciosViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: EspaciosRepository
    private lateinit var viewModel: EspaciosViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        val application = mock<Application>()
        viewModel = EspaciosViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testActualizarTextoBusqueda_llamaAlRepositorio() = runTest {
        val lista = listOf(
            EspacioNaturalEntity(
                "1", "Playa X", "playa", "Gijón", "desc",
                null, null, null, null, null, null, null, null, null, null, null, null, "", null
            )
        )
        whenever(repository.searchEspacios("Playa")).thenReturn(lista)

        val espaciosOriginalesField = viewModel.javaClass.getDeclaredField("espaciosOriginales")
        espaciosOriginalesField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (espaciosOriginalesField.get(viewModel) as MutableLiveData<List<EspacioNaturalEntity>>).value = lista

        viewModel.actualizarTextoBusqueda("Playa")
        advanceUntilIdle()

        verify(repository).searchEspacios("Playa")
    }

    @Test
    fun testToggleFavorito_invierteElEstado() = runTest {
        val espacio = EspacioNaturalEntity(
            "1", "Parque", "parque", "Oviedo", "desc",
            null, null, null, null, null, null, null, null, null, null, null, null, "", null,
            esFavorito = false
        )

        // Inicializamos la lista que usa actualizarTextoBusqueda()
        viewModel.setEspaciosOriginalesTest(listOf(espacio))

        viewModel.toggleFavorito(espacio)
        advanceUntilIdle()

        assertTrue(espacio.esFavorito)
        verify(repository).actualizarFavorito("1", true)
    }


    @Test
    fun testGetEspacioById_emiteElementoEsperado() = runTest {
        val espacio = EspacioNaturalEntity(
            "42", "Monte", "monte", "Aller", "desc",
            null, null, null, null, null, null, null, null, null, null, null, null, "", null
        )
        val lista = listOf(espacio)

        val allEspaciosField = viewModel.javaClass.getDeclaredField("allEspacios")
        allEspaciosField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (allEspaciosField.get(viewModel) as MutableLiveData<List<EspacioNaturalEntity>>).value = lista

        val observer = mock<Observer<EspacioNaturalEntity?>>()
        viewModel.getEspacioById("42").observeForever(observer)
        verify(observer).onChanged(espacio)
    }


}
