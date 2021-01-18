package com.example.instagram.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.adapter.AdapterPesquisa;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment {

    //Widget
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;

    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;
    private AdapterPesquisa adapterPesquisa;

    public PesquisaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa   = view.findViewById(R.id.recyclerPesquisa);

        //Configurações iniciais
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios");

        //Configura RecyclerView
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        recyclerPesquisa.setAdapter( adapterPesquisa );

        //Configura SearchView
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //Retorna resultados assim que o usuário clica em enviar/procurar
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { //Retorna resultados enquanto o usuário digita

                String textoDigitado = newText.toUpperCase();
                pesquisarUsuarios( textoDigitado );

                return true;
            }

            private void pesquisarUsuarios(String texto){

                //Limpar lista
                listaUsuarios.clear();

                //Pesquisa usuários caso tenha texto na pesquisa
                if ( texto.length() >= 2 ){

                    Query query = usuariosRef.orderByChild("nome")
                            .startAt(texto)
                            .endAt(texto + "\uf88f");

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //limpar lista
                            listaUsuarios.clear();

                            for ( DataSnapshot ds : snapshot.getChildren() ){
                                listaUsuarios.add( ds.getValue(Usuario.class) );
                            }

                            adapterPesquisa.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

        });

        return view;
    }
}
