package pl.org.seva.checkers.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import pl.org.seva.checkers.R
import pl.org.seva.checkers.databinding.FrGameBinding

class GameFragment : Fragment(R.layout.fr_game) {

    private lateinit var binding: FrGameBinding
    private val viewModel by viewModels<GameVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrGameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}
