package com.example.ecomate.UI.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecomate.Api.ApiConfigArticle
import com.example.ecomate.Api.ApiConfigDatabase
import com.example.ecomate.Api.ArticleRequest
import com.example.ecomate.Response.ArticleResponseItem
import com.example.ecomate.Response.UserChallengesResponse
import com.example.ecomate.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            binding.user.text = currentUser.displayName
            getNotStartedChallenge()
            getArticles("How to recycle plastic")
        }
    }

    private fun getNotStartedChallenge() {
        showLoading(true)
        val currentUser = auth.currentUser

        val client = ApiConfigDatabase.getApiService().getUserChallenges(currentUser!!.uid)
        client.enqueue(object : Callback<UserChallengesResponse> {
            override fun onResponse(
                call: Call<UserChallengesResponse>,
                response: Response<UserChallengesResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val notStartedChallenge =
                            responseBody.challengeList.first { it.challengeStatus == "notStarted" }
                        binding.tvChallengeName.text = notStartedChallenge.challengeTitle
                        binding.tvChallengeDesc.text = notStartedChallenge.challengeDesc
                        binding.tvChallengePoint.text = notStartedChallenge.challengePoints.toString()
                    }
                } else {
                    Log.e(GET_CHALLENGE, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UserChallengesResponse>, t: Throwable) {
                showLoading(false)
                Log.e(GET_CHALLENGE, "onFailure: ${t.message}")
            }
        })
    }

    private fun getArticles(text: String) {
        showLoading(true)

        val client = ApiConfigArticle.getApiService().ArticleRecomend(ArticleRequest(text))
        client.enqueue(object : Callback<List<ArticleResponseItem>> {
            override fun onResponse(
                call: Call<List<ArticleResponseItem>>,
                response: Response<List<ArticleResponseItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val article = responseBody.first()
                        binding.tvArticleTitle.text = article.title
                        binding.tvArticleContent.text = article.cleanContent
                        binding.cardArticle.setOnClickListener {
                            val url = article.articleLink
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            binding.root.context.startActivity(intent)
                        }
                    }
                } else {
                    Log.e(GET_ARTICLE, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ArticleResponseItem>>, t: Throwable) {
                showLoading(false)
                Log.e(GET_ARTICLE, "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(state: Boolean) { binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE }

    companion object {
        private const val GET_CHALLENGE = "Get Challenge"
        private const val GET_ARTICLE = "Get Article"
    }
}